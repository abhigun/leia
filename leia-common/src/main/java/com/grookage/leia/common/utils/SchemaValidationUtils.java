/*
 * Copyright (c) 2024. Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grookage.leia.common.utils;

import com.google.common.collect.Sets;
import com.grookage.leia.common.context.TypeVariableContext;
import com.grookage.leia.common.validation.ValidationContext;
import com.grookage.leia.common.validation.ValidationResponse;
import com.grookage.leia.common.violation.LeiaSchemaViolation;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.attributes.*;
import com.grookage.leia.models.schema.SchemaDetails;
import com.grookage.leia.models.schema.SchemaValidationType;
import com.grookage.leia.models.schema.SchemaValidationVisitor;
import com.grookage.leia.models.utils.CollectionUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class SchemaValidationUtils {
    private static final String TYPE_MISMATCH_MESSAGE = "Type mismatch, expected: %s, provided: %s";
    static Function<Class<?>, Function<SchemaAttribute, Boolean>> assignableCheckFunction =
            klass -> attribute -> ClassUtils.isAssignable(klass, attribute.getType().getAssignableClass());

    public ValidationResponse valid(final SchemaDetails schemaDetails,
                                           final Class<?> klass) {
        //TODO: Reuse reflections to save scan time
        return valid(schemaDetails, klass, input -> Set.of());
    }

    public ValidationResponse valid(final SchemaDetails schemaDetails,
                                           final Class<?> klass,
                                           Function<Class<?>, Set<Class<?>>> subTypeResolver) {
        final var validationContext = new ValidationContext();
        final var typeVariableContext = new TypeVariableContext();
        if (null != schemaDetails.getChildReferences()) {
            subTypeResolver.apply(klass).forEach(each -> {
                pushClassForValidation(each, validationContext, String.format("Missing schema definition annotation on  subclass %s of %s",
                        each.getSimpleName(), klass.getSimpleName()), null);
            });
        }
        if (null == schemaDetails.getParentReference()) {
            return valid(schemaDetails.getValidationType(), schemaDetails.getAttributes(), klass, null, validationContext, typeVariableContext);
        }
        final var parentClass = klass.getSuperclass();
        pushClassForValidation(parentClass, validationContext, String.format("Missing schema definition annotation on parent class %s of %s",
                parentClass.getSimpleName(), klass.getSimpleName()), null);
        final var fields = FieldUtils.getClassFields(klass);
        return valid(schemaDetails.getValidationType(), schemaDetails.getAttributes(), klass, fields, validationContext, typeVariableContext);
    }

    public ValidationResponse valid(final SchemaValidationType validationType,
                                           final Set<SchemaAttribute> attributes,
                                           final Class<?> klass) {
        return valid(validationType, attributes, klass, null, new ValidationContext(), new TypeVariableContext());
    }

    public ValidationResponse valid(final SchemaValidationType validationType,
                                           final Set<SchemaAttribute> attributes,
                                           final Class<?> klass,
                                           final List<Field> fields,
                                           final ValidationContext validationContext,
                                           final TypeVariableContext typeVariableContext) {
        validationContext.pushClass(klass);
        final var fieldList = fields == null ? FieldUtils.getAllFields(klass) : fields;
        validateSchemaStructure(validationType, attributes, fieldList, validationContext);
        attributes.forEach(each -> {
            final var field = FieldUtils.filter(each.getName(), fieldList);
            if (field.isEmpty()) {
                validationContext.addViolation("Missing Field", each.getName());
                return;
            }
            validateField(each, field.get(), validationType, validationContext, typeVariableContext);
        });
        validationContext.popClass();
        return ValidationResponse.builder()
                .violations(validationContext.getViolations())
                .classesToValidate(validationContext.getValidationQueue())
                .build();
    }

    private void validateField(final SchemaAttribute attribute,
                               final Field field,
                               final SchemaValidationType validationType,
                               final ValidationContext validationContext,
                               final TypeVariableContext typeVariableContext) {
        final var type = typeVariableContext.resolveType(field.getAnnotatedType());
        validateType(validationType, attribute, type, validationContext,
                typeVariableContext);
    }

    private void validateType(final SchemaValidationType validationType,
                              final SchemaAttribute attribute,
                              final AnnotatedType annotatedType,
                              final ValidationContext validationContext,
                              final TypeVariableContext typeVariableContext) {
        final var type = annotatedType.getType();
        if (type instanceof Class<?> klass) {
            validateClass(validationType, attribute, klass, validationContext);
        } else if (type instanceof ParameterizedType) {
            validateParameterizedType(validationType, attribute, (AnnotatedParameterizedType)annotatedType, validationContext, typeVariableContext);
        } else if (type instanceof GenericArrayType) {
            validateGenericArrayType(validationType, attribute, (AnnotatedArrayType) annotatedType, validationContext, typeVariableContext);
        } else {
            validationContext.addViolation("Unsupported class type: " + type);
        }
    }

    private void validateClass(final SchemaValidationType validationType,
                               final SchemaAttribute schemaAttribute,
                               final Class<?> klass,
                               final ValidationContext validationContext) {
        if (!isMatchingType(klass, schemaAttribute)) {
            validationContext.addViolation(String.format(TYPE_MISMATCH_MESSAGE, schemaAttribute.getType(), klass.getSimpleName()),
                    schemaAttribute.getName());
            return;
        }

        schemaAttribute.accept(new SchemaAttributeHandler<Void>(a -> null) {
            @Override
            public Void accept(ArrayAttribute attribute) {
                if (Objects.nonNull(attribute.getElementAttribute())) {
                    if (klass.isArray()) {
                        validateClass(validationType, attribute.getElementAttribute(), klass.getComponentType(), validationContext);
                        return null;
                    }
                    // Provided List, Set expected List<?>, Set<?>
                    validationContext.addViolation(String.format("Missing Type arguments, expected ParameterizedType:%s",
                            attribute.getElementAttribute().getType()), attribute.getName());
                }
                return null;
            }

            @Override
            public Void accept(MapAttribute attribute) {
                if (Objects.nonNull(attribute.getKeyAttribute()) || Objects.nonNull(attribute.getValueAttribute())) {
                    // Provided Map, expected Map<?,?>
                    validationContext.addViolation(String.format("Missing Type Arguments, expected parameterized Types key:%s value:%s",
                            attribute.getKeyAttribute().getType(), attribute.getValueAttribute().getType()), attribute.getName());
                }
                return null;
            }

            @Override
            public Void accept(ObjectAttribute attribute) {
                if (Objects.nonNull(attribute.getNestedAttributes())) {
                    valid(validationType, attribute.getNestedAttributes(), klass, null, validationContext, new TypeVariableContext());
                }
                return null;
            }

            @Override
            public Void accept(SchemaReferenceAttribute attribute) {
                pushClassForValidation(klass, validationContext, String.format("Missing schema definition annotation on referenced class %s",
                        klass.getSimpleName()), attribute.getName());
                return null;
            }
        });
    }

    private void validateParameterizedType(final SchemaValidationType validationType,
                                           final SchemaAttribute attribute,
                                           final AnnotatedParameterizedType annotatedParameterizedType,
                                           final ValidationContext validationContext,
                                           final TypeVariableContext typeVariableContext) {
        final var parameterizedType = (ParameterizedType) annotatedParameterizedType.getType();
        final var rawType = (Class<?>) parameterizedType.getRawType();
        if (attribute instanceof ArrayAttribute arrayAttribute) {
            if (arrayAttribute.getElementAttribute() == null) {
                return;
            }
            final var elementType = typeVariableContext.resolveType(annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]);
            validateType(validationType, arrayAttribute.getElementAttribute(), elementType, validationContext, typeVariableContext);
        } else if (attribute instanceof MapAttribute mapAttribute) {
            if (Objects.isNull(mapAttribute.getKeyAttribute()) || Objects.isNull(mapAttribute.getValueAttribute())) {
                return;
            }
            final var keyType = typeVariableContext.resolveType(annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]);
            final var valueType = typeVariableContext.resolveType(annotatedParameterizedType.getAnnotatedActualTypeArguments()[1]);
            validateType(validationType, mapAttribute.getKeyAttribute(), keyType, validationContext, typeVariableContext);
            validateType(validationType, mapAttribute.getValueAttribute(), valueType, validationContext, typeVariableContext);
        } else if (attribute instanceof ObjectAttribute objectAttribute) {
            if (CollectionUtils.isNullOrEmpty(objectAttribute.getNestedAttributes())) {
                return;
            }
            final var newContext = TypeVariableContext.from(rawType, annotatedParameterizedType, typeVariableContext);
            valid(validationType, objectAttribute.getNestedAttributes(), rawType,null, validationContext, newContext);
        } else {
            validationContext.addViolation(String.format(TYPE_MISMATCH_MESSAGE, attribute.getType(), parameterizedType), attribute.getName());
        }
    }

    private void validateGenericArrayType(final SchemaValidationType validationType,
                                          final SchemaAttribute attribute,
                                          final AnnotatedArrayType annotatedArrayType,
                                          final ValidationContext validationContext,
                                          final TypeVariableContext typeVariableContext) {
        if (attribute instanceof ArrayAttribute arrayAttribute) {
            final var elementType = typeVariableContext.resolveType(annotatedArrayType.getAnnotatedGenericComponentType());
            validateType(validationType, arrayAttribute.getElementAttribute(), elementType, validationContext, typeVariableContext);
            return;
        }
        validationContext.addViolation(String.format(TYPE_MISMATCH_MESSAGE, attribute.getType(), annotatedArrayType), attribute.getName());
    }

    private boolean isMatchingType(final Class<?> klass,
                                   final SchemaAttribute attribute) {
        return attribute.accept(new SchemaAttributeHandler<>(assignableCheckFunction.apply(klass)) {
            @Override
            public Boolean accept(ArrayAttribute attribute) {
                return klass.isArray() || ClassUtils.isAssignable(klass, Collection.class);
            }

            @Override
            public Boolean accept(ObjectAttribute attribute) {
                return !klass.equals(Object.class) || !Objects.nonNull(attribute.getNestedAttributes());
            }
        });
    }

    private void validateSchemaStructure(final SchemaValidationType validationType,
                                         final Set<SchemaAttribute> attributes,
                                         final List<Field> fields,
                                         final ValidationContext validationContext) {
        final var fieldNames = fields.stream()
                .map(Field::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        final var attributesListed = attributes.stream()
                .map(SchemaAttribute::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        validationType.accept(new SchemaValidationVisitor<>() {
            @Override
            public Void strict() {
                final var mismatchedAttributes = Sets.symmetricDifference(fieldNames, attributesListed);
                if (!mismatchedAttributes.isEmpty()) {
                    validationContext.addViolation(String.format("[STRICT] Validation: attributes not found or extra attributes :%s", mismatchedAttributes));
                }
                return null;
            }

            @Override
            public Void matching() {
                final var attributesMissing = Sets.difference(attributesListed, fieldNames);
                if (!attributesMissing.isEmpty()) {
                    validationContext.addViolation(String.format("[MATCHING] Validation: Missing attributes found :%s", attributesMissing));
                }
                return null;
            }
        });
    }

    private void pushClassForValidation(Class<?> klass, ValidationContext validationContext, String message, String path) {
        if (klass.isAnnotationPresent(SchemaDefinition.class)) {
            validationContext.validateClass(klass);
        } else {
            validationContext.addViolation(message, path);
        }
    }

    public boolean valid(final Class<?> klass,
                         final SchemaAttribute schemaAttribute) {
        return schemaAttribute.accept(new SchemaAttributeHandler<>(assignableCheckFunction.apply(klass)) {
        });
    }

}

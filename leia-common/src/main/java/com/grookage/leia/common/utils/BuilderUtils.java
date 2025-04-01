package com.grookage.leia.common.utils;

import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.annotations.SchemaRef;
import com.grookage.leia.models.annotations.attribute.Optional;
import com.grookage.leia.models.annotations.attribute.qualifiers.Encrypted;
import com.grookage.leia.models.annotations.attribute.qualifiers.PII;
import com.grookage.leia.models.annotations.attribute.qualifiers.ShortLived;
import com.grookage.leia.models.attributes.*;
import com.grookage.leia.models.qualifiers.EncryptedQualifier;
import com.grookage.leia.models.qualifiers.PIIQualifier;
import com.grookage.leia.models.qualifiers.QualifierInfo;
import com.grookage.leia.models.qualifiers.ShortLivedQualifier;
import com.grookage.leia.models.schema.SchemaReference;
import lombok.experimental.UtilityClass;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class BuilderUtils {
    public SchemaAttribute buildPrimitiveAttribute(final Class<?> klass,
                                                   final String name,
                                                   final Set<QualifierInfo> qualifiers,
                                                   final boolean optional) {
        if (klass == Integer.class || klass == int.class) {
            return new IntegerAttribute(name, optional, qualifiers);
        }
        if (klass == Boolean.class || klass == boolean.class) {
            return new BooleanAttribute(name, optional, qualifiers);
        }
        if (klass == Double.class || klass == double.class) {
            return new DoubleAttribute(name, optional, qualifiers);
        }
        if (klass == Long.class || klass == long.class) {
            return new LongAttribute(name, optional, qualifiers);
        }
        if (klass == Float.class || klass == float.class) {
            return new FloatAttribute(name, optional, qualifiers);
        }
        if (klass == Short.class || klass == short.class) {
            return new ShortAttribute(name, optional, qualifiers);
        }
        if (klass == Character.class || klass == char.class) {
            return new CharacterAttribute(name, optional, qualifiers);
        }
        if (klass == Byte.class || klass == byte.class) {
            return new ByteAttribute(name, optional, qualifiers);
        }

        throw new UnsupportedOperationException("Unsupported primitive class type: " + klass.getName());

    }

    public Set<String> getEnumValues(final Class<?> klass) {
        return Arrays.stream(klass.getEnumConstants())
                .map(enumConstant -> ((Enum<?>) enumConstant).name())
                .collect(Collectors.toSet());
    }

    public boolean isValidChain(Class<?> klass) {
        if (Objects.isNull(klass.getSuperclass())) {
            return true;
        }

        final var current = klass.getSuperclass();
        final var lastRef = isSchemaReference(klass);
        for (Class<?> c = klass; c != null; c = c.getSuperclass()) {
            if (!lastRef && isSchemaReference(current)) {
                return false;
            }
        }

        return true;
    }

    public List<Field> getFields(Class<?> klass) {
        if (isValidChain(klass)) {
            return FieldUtils.getClassFields(klass);
        }
        return FieldUtils.getAllFields(klass);
    }

    public Class<?> getRawType(final AnnotatedType annotatedType) {
        final var type = annotatedType.getType();
        if (type instanceof Class<?> klass)
            return klass;
        if (type instanceof ParameterizedType parameterizedType) {
            return (Class<?>) parameterizedType.getRawType();
        }
        if (type instanceof GenericArrayType genericArrayType) {
            return (Class<?>) genericArrayType.getGenericComponentType();
        }
        return null;
    }

    public boolean isOptional(final AnnotatedType annotatedType) {
        return annotatedType.isAnnotationPresent(Optional.class);
    }

    public boolean isSchemaReference(final Class<?> klass) {
        return klass.isAnnotationPresent(SchemaRef.class);
    }

    public boolean isSchemaReference(final AnnotatedType annotatedType) {
        return annotatedType.isAnnotationPresent(SchemaRef.class);
    }

    public boolean isOptional(final Class<?> klass) {
        return klass.isAnnotationPresent(Optional.class);
    }

    public boolean isOptional(final Field field) {
        return field.isAnnotationPresent(Optional.class);
    }
    public Set<QualifierInfo> getQualifiers(final AnnotatedType annotatedType) {
        Set<QualifierInfo> qualifiers = new HashSet<>();
        if (annotatedType.isAnnotationPresent(Encrypted.class)) {
            qualifiers.add(new EncryptedQualifier());
        }
        if (annotatedType.isAnnotationPresent(PII.class)) {
            qualifiers.add(new PIIQualifier());
        }
        if (annotatedType.isAnnotationPresent(ShortLived.class)) {
            final var shortLived = annotatedType.getAnnotation(ShortLived.class);
            qualifiers.add(new ShortLivedQualifier(shortLived.ttlSeconds()));
        }
        return qualifiers;
    }

    public Set<QualifierInfo> getQualifiers(final Field field) {
        Set<QualifierInfo> qualifiers = new HashSet<>();
        if (field.isAnnotationPresent(Encrypted.class)) {
            qualifiers.add(new EncryptedQualifier());
        }
        if (field.isAnnotationPresent(PII.class)) {
            qualifiers.add(new PIIQualifier());
        }
        if (field.isAnnotationPresent(ShortLived.class)) {
            final var shortLived = field.getAnnotation(ShortLived.class);
            qualifiers.add(new ShortLivedQualifier(shortLived.ttlSeconds()));
        }
        return qualifiers;
    }

    public SchemaReference getSchemaReference(final Class<?> klass) {
        if (!klass.isAnnotationPresent(SchemaDefinition.class)) {
            return null;
        }
        final var schemaDefinition = klass.getAnnotation(SchemaDefinition.class);
        return SchemaReference.builder()
                .namespace(schemaDefinition.namespace())
                .name(schemaDefinition.name())
                .build();
    }

    public SchemaReference getSchemaReference(final AnnotatedType annotatedType) {
        if (!isSchemaReference(annotatedType)) {
            return null;
        }

        final var klass = getRawType(annotatedType);
        return getSchemaReference(klass);
    }

    public Set<QualifierInfo> getQualifiers(final Class<?> klass) {
        Set<QualifierInfo> qualifiers = new HashSet<>();
        if (klass.isAnnotationPresent(Encrypted.class)) {
            qualifiers.add(new EncryptedQualifier());
        }
        if (klass.isAnnotationPresent(PII.class)) {
            qualifiers.add(new PIIQualifier());
        }
        if (klass.isAnnotationPresent(ShortLived.class)) {
            final var shortLived = klass.getAnnotation(ShortLived.class);
            qualifiers.add(new ShortLivedQualifier(shortLived.ttlSeconds()));
        }
        return qualifiers;
    }
}

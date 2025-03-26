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

package com.grookage.leia.validator;

import com.grookage.leia.common.exception.SchemaValidationException;
import com.grookage.leia.common.exception.ValidationErrorCode;
import com.grookage.leia.common.utils.ReflectionUtils;
import com.grookage.leia.common.utils.SchemaValidationUtils;
import com.grookage.leia.common.violation.LeiaSchemaViolation;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.attributes.SchemaAttribute;
import com.grookage.leia.models.schema.SchemaDetails;
import com.grookage.leia.models.schema.SchemaKey;
import com.grookage.leia.models.utils.SchemaUtils;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class StaticSchemaValidator implements LeiaSchemaValidator {

    private final ConcurrentHashMap<SchemaKey, Boolean> validationRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SchemaKey, Class<?>> klassRegistry = new ConcurrentHashMap<>();
    private final Supplier<List<SchemaDetails>> supplier;
    private final Set<String> packageRoots;

    @Builder
    public StaticSchemaValidator(Supplier<List<SchemaDetails>> supplier,
                                 Set<String> packageRoots) {
        this.supplier = supplier;
        this.packageRoots = packageRoots;
    }

    @SneakyThrows
    private List<LeiaSchemaViolation> validate(final SchemaKey schemaKey, Class<?> klass, Reflections reflections) {
        final var details = SchemaUtils.getMatchingSchema(supplier.get(), schemaKey).orElse(null);
        if (null == details) {
            throw SchemaValidationException.error(ValidationErrorCode.NO_SCHEMA_FOUND,
                    String.format("No schema found with key: %s", schemaKey.getReferenceId()));
        }
        final Function<Class<?>, Set<Class<?>>> subTypeResolver = input -> ReflectionUtils.getImmediateSubTypes(reflections, input);
        val validationResponse = SchemaValidationUtils.valid(details, klass, subTypeResolver);
        validationResponse.getClassesToValidate().forEach(each -> {
            val annotation = each.getAnnotation(SchemaDefinition.class);
            val key = SchemaKey.builder()
                    .schemaName(annotation.name())
                    .version(annotation.version())
                    .namespace(annotation.namespace())
                    .build();
            if (!validationRegistry.contains(key)) {
                val response = SchemaValidationUtils.valid(details, klass, subTypeResolver);
                validationResponse.getViolations().addAll(response.getViolations());
                validationResponse.getClassesToValidate().addAll(response.getClassesToValidate());
            }
        });
        return validationResponse.getViolations();
    }

    @Override
    public void start() {
        log.info("Starting the schema validator");
        Map<SchemaKey, List<LeiaSchemaViolation>> violations = new HashMap<>();
        packageRoots.forEach(handlerPackage -> {
            final var reflections = new Reflections(handlerPackage);
            final var annotatedClasses = reflections.getTypesAnnotatedWith(SchemaDefinition.class);
            annotatedClasses.forEach(annotatedClass -> {
                final var annotation = annotatedClass.getAnnotation(SchemaDefinition.class);
                final var schemaKey = SchemaKey.builder()
                        .schemaName(annotation.name())
                        .version(annotation.version())
                        .namespace(annotation.namespace())
                        .build();
                klassRegistry.putIfAbsent(schemaKey, annotatedClass);
                final var schemaViolations = validate(schemaKey, annotatedClass, reflections);
                validationRegistry.putIfAbsent(schemaKey, schemaViolations.isEmpty());
                if (!schemaViolations.isEmpty()) {
                    violations.putIfAbsent(schemaKey, schemaViolations);
                }
            });
        });
        if (!violations.isEmpty()) {
            log.error("Found invalid schemas. Please fix the following schemas to start the bundle {}", violations);
            throw SchemaValidationException.builder()
                    .errorCode(ValidationErrorCode.INVALID_SCHEMAS)
                    .context(Map.of("schemaViolations", violations))
                    .build();
        }
    }

    @Override
    public void stop() {
        log.info("Stopping the schema validator");
    }

    @Override
    public boolean valid(SchemaKey schemaKey) {
        return validationRegistry.computeIfAbsent(schemaKey, key -> Boolean.FALSE);
    }

    @Override
    public Optional<Class<?>> getKlass(SchemaKey schemaKey) {
        return Optional.ofNullable(klassRegistry.get(schemaKey));
    }

}

package com.grookage.leia.common.utils;

import com.grookage.leia.common.builder.SchemaBuilder;
import com.grookage.leia.common.exception.ValidationErrorCode;
import com.grookage.leia.common.stubs.NestedStub;
import com.grookage.leia.common.stubs.PIIData;
import com.grookage.leia.common.violation.ViolationContext;
import com.grookage.leia.models.GenericResponse;
import com.grookage.leia.models.ResourceHelper;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.attributes.*;
import com.grookage.leia.models.schema.SchemaDetails;
import com.grookage.leia.models.schema.SchemaType;
import com.grookage.leia.models.schema.SchemaValidationType;
import com.grookage.leia.models.schema.engine.SchemaState;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class SchemaValidationUtilsTest {
    @Test
    @SneakyThrows
    void testSchemaValidator() {
        final var schemaDetails = ResourceHelper
                .getResource("validSchema.json", SchemaDetails.class);
        Assertions.assertNotNull(schemaDetails);
        Assertions.assertTrue(SchemaValidationUtils.valid(schemaDetails, ValidTestClass.class).isEmpty());
        schemaDetails.setValidationType(SchemaValidationType.STRICT);
        Assertions.assertFalse(SchemaValidationUtils.valid(schemaDetails, ValidTestClass.class).isEmpty());
    }

    @Test
    @SneakyThrows
    void testInvalidMatchingSchema() {
        final var schemaDetails = ResourceHelper
                .getResource("validSchema.json", SchemaDetails.class);
        schemaDetails.setValidationType(SchemaValidationType.MATCHING);
        Assertions.assertNotNull(schemaDetails);
        Assertions.assertFalse(SchemaValidationUtils.valid(schemaDetails, InvalidTestClass.class).isEmpty());
    }

    @Test
    void testAllFields() {
        final var booleanAttribute = new BooleanAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Boolean.class, booleanAttribute));

        final var byteAttribute = new ByteAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Byte.class, byteAttribute));

        final var characterAttribute = new CharacterAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Character.class, characterAttribute));

        final var doubleAttribute = new DoubleAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Double.class, doubleAttribute));

        final var enumAttribute = new EnumAttribute("testAttribute", true, null, Set.of());
        Assertions.assertTrue(SchemaValidationUtils.valid(ValidationErrorCode.class, enumAttribute));

        final var floatAttribute = new FloatAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Float.class, floatAttribute));

        final var integerAttribute = new IntegerAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Integer.class, integerAttribute));

        final var longAttribute = new LongAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Long.class, longAttribute));

        final var shortAttribute = new ShortAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Short.class, shortAttribute));

        final var stringAttribute = new StringAttribute("testAttribute", true, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(String.class, stringAttribute));

        final var arrayAttribute = new ArrayAttribute("testAttribute", true, null, stringAttribute);
        Assertions.assertTrue(SchemaValidationUtils.valid(Set.class, arrayAttribute));

        final var mapAttribute = new MapAttribute("testAttribute", true, null, stringAttribute, stringAttribute);
        Assertions.assertTrue(SchemaValidationUtils.valid(Map.class, mapAttribute));

        final var plainObjectAttribute = new ObjectAttribute("testAttribute", true, null, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(Object.class, plainObjectAttribute));

        final var objectAttribute = new ObjectAttribute("testAttribute", true, null, Set.of(stringAttribute));
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaDetails.class, objectAttribute));

        Assertions.assertFalse(SchemaValidationUtils.valid(Long.class, integerAttribute));
        Assertions.assertTrue(SchemaValidationUtils.valid(Long.class, objectAttribute));


    }

    @Test
    void testParametrizedArray() {
        final var stringAttribute = new StringAttribute("stringAttribute", true, null);
        final var arrayAttribute = new ArrayAttribute("arrayAttribute", true, null, stringAttribute);
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(arrayAttribute),
                SetTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(arrayAttribute),
                ListTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(arrayAttribute),
                ArrayTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertFalse(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(arrayAttribute),
                RawSetTestClass.class, new ViolationContext()).isEmpty());
    }

    @Test
    void testRawArray() {
        final var arrayAttribute = new ArrayAttribute("arrayAttribute", true, null, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING,
                Set.of(arrayAttribute), RawSetTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING,
                Set.of(arrayAttribute), SetTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING,
                Set.of(arrayAttribute), ListTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING,
                Set.of(arrayAttribute), ArrayTestClass.class, new ViolationContext()).isEmpty());
    }

    @Test
    void testParametrizedMap() {
        final var keyAttribute = new StringAttribute("keyAttribute", true, null);
        final var valueAttribute = new StringAttribute("valueAttribute", true, null);
        final var mapAttribute = new MapAttribute("mapAttribute", true, null, keyAttribute, valueAttribute);
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(mapAttribute),
                MapTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(mapAttribute),
                ConcurrentMapTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertFalse(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(mapAttribute),
                RawMapTestClass.class, new ViolationContext()).isEmpty());
    }

    @Test
    void testRawMap() {
        final var mapAttribute = new MapAttribute("mapAttribute", true, null, null, null);
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(mapAttribute),
                RawMapTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(mapAttribute),
                MapTestClass.class, new ViolationContext()).isEmpty());
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(mapAttribute),
                ConcurrentMapTestClass.class, new ViolationContext()).isEmpty());
    }

    @Test
    @SneakyThrows
    void testNestedObject() {
        final var schemaDetails = ResourceHelper
                .getResource("validNestedSchema.json", SchemaDetails.class);
        schemaDetails.setValidationType(SchemaValidationType.MATCHING);
        Assertions.assertTrue(SchemaValidationUtils.valid(schemaDetails, ValidObjectTestClass.class).isEmpty());
        Assertions.assertFalse(SchemaValidationUtils.valid(schemaDetails, InvalidObjectTestClass.class).isEmpty());
    }

    @Test
    void testGenericArrayType() {
        final var stringAttribute = new StringAttribute("stringAttribute", true, null);
        final var listAttribute = new ArrayAttribute("listAttribute", true, null, stringAttribute);
        final var arrayAttribute = new ArrayAttribute("arrayAttribute", true, null, listAttribute);
        Assertions.assertTrue(SchemaValidationUtils.valid(SchemaValidationType.MATCHING, Set.of(arrayAttribute),
                GenericArrayTestClass.class, new ViolationContext()).isEmpty());
    }

    @Test
    void testParameterizedObjectClass() {

        final var createSchemaRequest = SchemaBuilder.buildSchemaRequest(ParameterizedObjectClass.class).orElse(null);
        final var schemaDetails = SchemaDetails.builder()
                .namespace(createSchemaRequest.getNamespace())
                .schemaName(createSchemaRequest.getSchemaName())
                .version("V222")
                .schemaState(SchemaState.CREATED)
                .schemaType(createSchemaRequest.getSchemaType())
                .description(createSchemaRequest.getDescription())
                .attributes(createSchemaRequest.getAttributes())
                .validationType(createSchemaRequest.getValidationType())
                .transformationTargets(createSchemaRequest.getTransformationTargets())
                .build();
        SchemaValidationUtils.valid(schemaDetails, ParameterizedObjectClass.class);
    }

    @SneakyThrows
    @Test
    void testInvalidNestedStubSchema() {
        final var schemaDetails = ResourceHelper.getResource("invalidNestedStubSchema.json", SchemaDetails.class);
        final var violations = SchemaValidationUtils.valid(schemaDetails, NestedStub.class);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(4, violations.size());
        final var nestedStubViolations = violations.stream()
                .filter(leiaSchemaViolation -> leiaSchemaViolation.rootKlass().equals(NestedStub.class))
                .toList();
        Assertions.assertEquals(3, nestedStubViolations.size());
        final var piiDataViolations = violations.stream()
                .filter(leiaSchemaViolation -> leiaSchemaViolation.rootKlass().equals(PIIData.class))
                .toList();
        Assertions.assertEquals(1, piiDataViolations.size());
    }

    enum TestEnum {
        TEST_ENUM
    }

    static class ValidTestClass {
        Set<String> testAttribute;
        TestEnum testAttribute2;
        String testAttribute3;
    }

    static class InvalidTestClass {
        Set<String> testAttribute;
    }

    static class SetTestClass {
        Set<String> arrayAttribute;
    }

    static class ListTestClass {
        List<String> arrayAttribute;
    }

    static class ArrayTestClass {
        String[] arrayAttribute;
    }

    static class RawSetTestClass {
        Set arrayAttribute;
    }

    static class MapTestClass {
        Map<String, String> mapAttribute;
    }

    static class ConcurrentMapTestClass {
        ConcurrentHashMap<String, String> mapAttribute;
    }

    static class RawMapTestClass {
        Map mapAttribute;
    }

    static class ValidObjectTestClass {
        String stringAttribute;
        ValidNestedObjectTestClass nestedObjectAttribute;
    }

    static class ValidNestedObjectTestClass {
        Integer integerAttribute;
    }

    static class InvalidObjectTestClass {
        String stringAttribute;
        InvalidNestedObjectTestClass nestedObjectAttribute;
    }

    static class InvalidNestedObjectTestClass {
        String integerAttribute;
    }

    static class GenericArrayTestClass {
        List<String>[] arrayAttribute;
    }

    @SchemaDefinition(
            name = ParameterizedObjectClass.NAME,
            namespace = ParameterizedObjectClass.NAMESPACE,
            version = ParameterizedObjectClass.VERSION,
            description = ParameterizedObjectClass.DESCRIPTION,
            type = SchemaType.JSON,
            validation = SchemaValidationType.MATCHING
    )
    static class ParameterizedObjectClass {
        static final String NAME = "TEST_RECORD";
        static final String NAMESPACE = "test";
        static final String VERSION = "v1";
        static final String DESCRIPTION = "Test Record";
        GenericResponse<Pair<String, Integer>> response;
        Pair<String, Integer> pair;
    }



}
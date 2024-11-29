package com.grookage.leia.client.utils;

import com.grookage.leia.models.annotations.Optional;
import com.grookage.leia.models.annotations.qualifiers.Encrypted;
import com.grookage.leia.models.annotations.qualifiers.PII;
import com.grookage.leia.models.attributes.ArrayAttribute;
import com.grookage.leia.models.attributes.BooleanAttribute;
import com.grookage.leia.models.attributes.ByteAttribute;
import com.grookage.leia.models.attributes.DoubleAttribute;
import com.grookage.leia.models.attributes.EnumAttribute;
import com.grookage.leia.models.attributes.FloatAttribute;
import com.grookage.leia.models.attributes.IntegerAttribute;
import com.grookage.leia.models.attributes.LongAttribute;
import com.grookage.leia.models.attributes.MapAttribute;
import com.grookage.leia.models.attributes.ObjectAttribute;
import com.grookage.leia.models.attributes.SchemaAttribute;
import com.grookage.leia.models.attributes.SchemaAttributeAcceptor;
import com.grookage.leia.models.attributes.StringAttribute;
import com.grookage.leia.models.qualifiers.QualifierInfo;
import com.grookage.leia.models.qualifiers.QualifierType;
import com.grookage.leia.models.qualifiers.ShortLivedQualifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class SchemaUtilTest {

    @Test
    void testSchemaAttributes_WithPrimitiveClass() {
        final var schemaAttributeSet = SchemaUtil.buildSchemaAttributes(TestWithPrimitive.class);
        Assertions.assertNotNull(schemaAttributeSet);
        Assertions.assertEquals(2, schemaAttributeSet.size());
        final var nameAttribute = new StringAttribute("name", true, new HashSet<>());
        equals(nameAttribute, filter(schemaAttributeSet, "name").orElse(null));
        final var idAttribute = new IntegerAttribute("id", false, new HashSet<>());
        equals(idAttribute, filter(schemaAttributeSet, "id").orElse(null));
    }

    @Test
    void testSchemaAttributes_WithRecordClass() {
        final var schemaAttributeSet = SchemaUtil.buildSchemaAttributes(TestRecord.class);
        Assertions.assertNotNull(schemaAttributeSet);
        Assertions.assertEquals(2, schemaAttributeSet.size());
        final var nameAttribute = new StringAttribute("name", false, Set.of(new com.grookage.leia.models.qualifiers.PIIQualifier()));
        equals(nameAttribute, filter(schemaAttributeSet, "name").orElse(null));
        final var idAttribute = new IntegerAttribute("id", true, new HashSet<>());
        equals(idAttribute, filter(schemaAttributeSet, "id").orElse(null));
    }

    @Test
    void testSchemaAttributes_WithNestedObject() {
        final var schemaAttributes = SchemaUtil.buildSchemaAttributes(TestWithNested.class);
        Assertions.assertFalse(schemaAttributes.isEmpty());
        Assertions.assertEquals(6, schemaAttributes.size());
        final var nameAttribute = new StringAttribute("name", false, new HashSet<>());
        equals(nameAttribute, filter(schemaAttributes, "name").orElse(null));

        final var idAttribute = new IntegerAttribute("id", false, new HashSet<>());
        equals(idAttribute, filter(schemaAttributes, "id").orElse(null));

        final var testPIIDataAttributes = new HashSet<SchemaAttribute>();
        final var piiNameAttribute = new StringAttribute("name", false, new HashSet<>());
        final var accountNumberAttribute = new StringAttribute("accountNumber", false, Set.of(new com.grookage.leia.models.qualifiers.EncryptedQualifier()));
        testPIIDataAttributes.add(piiNameAttribute);
        testPIIDataAttributes.add(accountNumberAttribute);
        final var piiDataAttribute = new ObjectAttribute("piiData", false, Set.of(new com.grookage.leia.models.qualifiers.PIIQualifier()), testPIIDataAttributes);
        equals(piiDataAttribute, filter(schemaAttributes, "piiData").orElse(null));

        final var testRecordAttributes = new HashSet<SchemaAttribute>();
        final var recordNameAttribute = new StringAttribute("name", false, Set.of(new com.grookage.leia.models.qualifiers.PIIQualifier()));
        final var recordIdAttribute = new IntegerAttribute("id", true, new HashSet<>());
        testRecordAttributes.add(recordNameAttribute);
        testRecordAttributes.add(recordIdAttribute);
        final var testRecordAttribute = new ObjectAttribute("testRecord", false, Set.of(new com.grookage.leia.models.qualifiers.EncryptedQualifier()),
                testRecordAttributes);
        equals(testRecordAttribute, filter(schemaAttributes, "testRecord").orElse(null));

        final var enumClassAttribute = new EnumAttribute("enumClass", false, new HashSet<>(), Set.of(EnumClass.ONE.name(),
                EnumClass.TWO.name()));
        equals(enumClassAttribute, filter(schemaAttributes, "enumClass").orElse(null));

        final var phoneNoAttribute = new StringAttribute("phoneNumber", false, Set.of(new com.grookage.leia.models.qualifiers.PIIQualifier()));
        equals(phoneNoAttribute, filter(schemaAttributes, "phoneNumber").orElse(null));
    }

    @Test
    void testSchemaAttributes_WithParameterizedType() {
        final var schemaAttributes = SchemaUtil.buildSchemaAttributes(TestWithParameterized.class);
        Assertions.assertNotNull(schemaAttributes);
        Assertions.assertEquals(3, schemaAttributes.size());

        final var valuesAttributes = new ArrayAttribute("values", false, new HashSet<>(),
                new StringAttribute("element", false, new HashSet<>()));
        equals(valuesAttributes, filter(schemaAttributes, "values").orElse(null));

        final var testPIIDataAttributes = new HashSet<SchemaAttribute>();
        final var piiNameAttribute = new StringAttribute("name", false, new HashSet<>());
        final var accountNumberAttribute = new StringAttribute("accountNumber", false, Set.of(new com.grookage.leia.models.qualifiers.EncryptedQualifier()));
        testPIIDataAttributes.add(piiNameAttribute);
        testPIIDataAttributes.add(accountNumberAttribute);
        final var piiDataListAttribute = new ArrayAttribute("piiDataList", false, Set.of(new com.grookage.leia.models.qualifiers.PIIQualifier()),
                new ObjectAttribute("element", false, Set.of(new com.grookage.leia.models.qualifiers.PIIQualifier()), testPIIDataAttributes));
        equals(piiDataListAttribute, filter(schemaAttributes, "piiDataList").orElse(null));

        final var mapAttribute = new MapAttribute("map", false, Set.of(new com.grookage.leia.models.qualifiers.EncryptedQualifier()),
                new EnumAttribute("key", false, new HashSet<>(), Set.of(EnumClass.ONE.name(), EnumClass.TWO.name())),
                new StringAttribute("value", false, new HashSet<>()));
        equals(mapAttribute, filter(schemaAttributes, "map").orElse(null));
    }

    enum EnumClass {
        ONE,
        TWO
    }

    @PII
    static class TestPIIData {
        String name;
        @Encrypted
        String accountNumber;
    }

    record TestRecord(@PII String name,
                             @Optional int id) {

    }

    static class TestWithPrimitive {
        @Optional
        String name;
        int id;
    }

    static class TestWithNested {
        String name;
        int id;
        @PII
        TestPIIData piiData;
        @Encrypted
        TestRecord testRecord;
        EnumClass enumClass;
        @PII
        String phoneNumber;
    }

    static class TestWithParameterized {
        String[] values;
        @PII
        List<TestPIIData> piiDataList;
        @Encrypted
        Map<EnumClass, String> map;
    }

    private java.util.Optional<SchemaAttribute> filter(Set<SchemaAttribute> schemaAttributes,
                                                       String name) {
        return schemaAttributes.stream()
                .filter(schemaAttribute -> schemaAttribute.getName().equals(name))
                .findFirst();
    }

    private void equals(SchemaAttribute expected,
                        SchemaAttribute original) {
        if (Objects.isNull(expected) && Objects.isNull(original)) {
            return;
        }
        Assertions.assertEquals(expected.getType(), original.getType(), "Type mismatch");
        Assertions.assertEquals(expected.getName(), original.getName(), "Name mismatch");
        Assertions.assertEquals(expected.isOptional(), original.isOptional(), "Optionality mismatch");

        // Compare QualifierInfo
        equals(expected.getQualifiers(), original.getQualifiers());

        // Accept the expected attribute type and perform specific validations
        expected.accept(new SchemaAttributeAcceptor<Void>() {
            @Override
            public Void accept(BooleanAttribute attribute) {
                Assertions.assertInstanceOf(BooleanAttribute.class, original, "Original is not BooleanAttribute");
                return null;
            }

            @Override
            public Void accept(ByteAttribute attribute) {
                Assertions.assertInstanceOf(ByteAttribute.class, original, "Original is not ByteAttribute");
                return null;
            }

            @Override
            public Void accept(DoubleAttribute attribute) {
                Assertions.assertInstanceOf(DoubleAttribute.class, original, "Original is not DoubleAttribute");
                return null;
            }

            @Override
            public Void accept(EnumAttribute attribute) {
                Assertions.assertInstanceOf(EnumAttribute.class, original, "Original is not EnumAttribute");
                EnumAttribute originalEnum = (EnumAttribute) original;
                Assertions.assertEquals(attribute.getValues(), originalEnum.getValues(), "Enum values mismatch");
                return null;
            }

            @Override
            public Void accept(FloatAttribute attribute) {
                Assertions.assertInstanceOf(FloatAttribute.class, original, "Original is not FloatAttribute");
                return null;
            }

            @Override
            public Void accept(IntegerAttribute attribute) {
                Assertions.assertInstanceOf(IntegerAttribute.class, original, "Original is not IntegerAttribute");
                return null;
            }

            @Override
            public Void accept(LongAttribute attribute) {
                Assertions.assertInstanceOf(LongAttribute.class, original, "Original is not LongAttribute");
                return null;
            }

            @Override
            public Void accept(StringAttribute attribute) {
                Assertions.assertInstanceOf(StringAttribute.class, original, "Original is not StringAttribute");
                return null;
            }

            @Override
            public Void accept(ArrayAttribute attribute) {
                Assertions.assertInstanceOf(ArrayAttribute.class, original, "Original is not ArrayAttribute");
                ArrayAttribute originalArray = (ArrayAttribute) original;
                SchemaUtilTest.this.equals(attribute.getElementAttribute(), originalArray.getElementAttribute()); // Recursive comparison for elementAttribute
                return null;
            }

            @Override
            public Void accept(MapAttribute attribute) {
                Assertions.assertInstanceOf(MapAttribute.class, original, "Original is not MapAttribute");
                MapAttribute originalMap = (MapAttribute) original;
                SchemaUtilTest.this.equals(attribute.getKeyAttribute(), originalMap.getKeyAttribute()); // Recursive comparison for key
                SchemaUtilTest.this.equals(attribute.getValueAttribute(), originalMap.getValueAttribute()); // Recursive comparison for value
                return null;
            }

            @Override
            public Void accept(ObjectAttribute attribute) {
                Assertions.assertInstanceOf(ObjectAttribute.class, original, "Original is not ObjectAttribute");
                ObjectAttribute originalObject = (ObjectAttribute) original;

                // Recursive comparison of nested attributes
                Assertions.assertEquals(attribute.getNestedAttributes().size(), originalObject.getNestedAttributes().size(),
                        "Nested attributes size mismatch");
                Iterator<SchemaAttribute> expectedIterator = attribute.getNestedAttributes().iterator();
                Iterator<SchemaAttribute> originalIterator = originalObject.getNestedAttributes().iterator();

                while (expectedIterator.hasNext() && originalIterator.hasNext()) {
                    SchemaUtilTest.this.equals(expectedIterator.next(), originalIterator.next());
                }
                return null;
            }
        });
    }

    private void equals(Set<QualifierInfo> expected,
                        Set<QualifierInfo> original) {
        Assertions.assertNotNull(expected, "Expected qualifiers should not be null");
        Assertions.assertNotNull(original, "Actual qualifiers should not be null");
        Assertions.assertEquals(expected.size(), original.size(), "Qualifier sets size mismatch");

        expected.forEach(expectedQualifier -> {
            java.util.Optional<QualifierInfo> matchingQualifier = filter(original, expectedQualifier.getType());

            Assertions.assertTrue(matchingQualifier.isPresent(),
                    "Missing qualifier of type: " + expectedQualifier.getType());

            if (expectedQualifier.getType() == QualifierType.SHORT_LIVED) {
                Assertions.assertInstanceOf(ShortLivedQualifier.class, matchingQualifier.get(), "Actual SHORT_LIVED qualifier must be of type ShortLivedQualifier");

                ShortLivedQualifier expectedShortLived = (ShortLivedQualifier) expectedQualifier;
                ShortLivedQualifier actualShortLived = (ShortLivedQualifier) matchingQualifier.get();

                Assertions.assertEquals(expectedShortLived.getTtlSeconds(), actualShortLived.getTtlSeconds(),
                        "Mismatch in TTL seconds for SHORT_LIVED qualifier");
            }
        });
    }

    private java.util.Optional<QualifierInfo> filter(Set<QualifierInfo> qualifiers,
                                                     QualifierType type) {
        return qualifiers.stream()
                .filter(qualifierInfo -> qualifierInfo.getType().equals(type))
                .findFirst();
    }
}
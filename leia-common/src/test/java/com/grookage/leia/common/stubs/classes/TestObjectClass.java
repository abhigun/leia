package com.grookage.leia.common.stubs.classes;

import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.annotations.attribute.Optional;
import com.grookage.leia.models.annotations.attribute.qualifiers.Encrypted;
import com.grookage.leia.models.annotations.attribute.qualifiers.PII;
import com.grookage.leia.models.schema.SchemaType;
import com.grookage.leia.models.schema.SchemaValidationType;

import java.time.LocalDateTime;
import java.util.Date;

@SchemaDefinition(
        name = TestObjectClass.NAME,
        namespace = TestObjectClass.NAMESPACE,
        version = TestObjectClass.VERSION,
        description = TestObjectClass.DESCRIPTION,
        type = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
public class TestObjectClass {
    public static final String NAME = "TEST_OBJECT";
    public static final String NAMESPACE = "test";
    public static final String VERSION = "v1";
    public static final String DESCRIPTION = "Test Object";

    int id;
    String name;
    @PII
    @Encrypted
    String accountNumber;
    long ttl;
    @Optional
    String accountId;
    Date date;
    LocalDateTime localDateTime;
}

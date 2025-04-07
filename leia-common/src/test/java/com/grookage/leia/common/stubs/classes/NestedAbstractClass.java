package com.grookage.leia.common.stubs.classes;

import com.grookage.leia.common.utils.Constants;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.schema.SchemaType;
import com.grookage.leia.models.schema.SchemaValidationType;

@SchemaDefinition(
        name = NestedAbstractClass.NAME,
        namespace = Constants.NAMESPACE,
        orgId = Constants.ORG_ID,
        tenantId = Constants.TENANT,
        type = Constants.TYPE,
        version = NestedAbstractClass.VERSION,
        description = NestedAbstractClass.DESCRIPTION,
        schemaType = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
public abstract class NestedAbstractClass {
    public static final String NAME = "NESTED_ABSTRACT_CLASS";
    public static final String VERSION = "V0";
    public static final String DESCRIPTION = "Nested Abstract Class";
    String name;
}

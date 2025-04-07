package com.grookage.leia.common.stubs.classes;

import com.grookage.leia.common.utils.Constants;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.schema.SchemaType;
import com.grookage.leia.models.schema.SchemaValidationType;

@SchemaDefinition(
        name = NestedAbstractClassImpl.NAME,
        namespace = Constants.NAMESPACE,
        orgId = Constants.ORG_ID,
        tenantId = Constants.TENANT,
        type = Constants.TYPE,
        version = NestedAbstractClassImpl.VERSION,
        description = NestedAbstractClassImpl.DESCRIPTION,
        schemaType = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
public class NestedAbstractClassImpl extends NestedAbstractClass {
    public static final String NAME = "NESTED_ABSTRACT_CLASS_IMPL";
    public static final String VERSION = "V0";
    public static final String DESCRIPTION = "Nested Abstract Class Impl";
    String firstName;
}

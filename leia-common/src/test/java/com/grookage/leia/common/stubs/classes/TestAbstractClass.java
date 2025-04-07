package com.grookage.leia.common.stubs.classes;

import com.grookage.leia.common.utils.Constants;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.annotations.SchemaRef;
import com.grookage.leia.models.schema.SchemaType;
import com.grookage.leia.models.schema.SchemaValidationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@SchemaDefinition(
        name = TestAbstractClass.NAME,
        namespace = Constants.NAMESPACE,
        orgId = Constants.ORG_ID,
        tenantId = Constants.TENANT,
        type = Constants.TYPE,
        version = TestAbstractClass.VERSION,
        description = TestAbstractClass.DESCRIPTION,
        schemaType = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestAbstractClass {
    public static final String NAME = "TEST_ABSTRACT_CLASS";
    public static final String VERSION = "V0";
    public static final String DESCRIPTION = "Abstract Data class";
    @SchemaRef
    NestedAbstractClass nestedAbstractClass;
    @SchemaRef
    NestedAbstractClassImpl nestedAbstractClassImpl;
    List<@SchemaRef NestedAbstractClassImpl> nestedAbstractClasses;
    List<String> strings;
}

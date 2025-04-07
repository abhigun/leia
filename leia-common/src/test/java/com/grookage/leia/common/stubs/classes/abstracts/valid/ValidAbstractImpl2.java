package com.grookage.leia.common.stubs.classes.abstracts.valid;

import com.grookage.leia.common.stubs.PIIData;
import com.grookage.leia.common.stubs.classes.TestObjectClass;
import com.grookage.leia.common.utils.Constants;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.annotations.SchemaRef;
import com.grookage.leia.models.schema.SchemaType;
import com.grookage.leia.models.schema.SchemaValidationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@SchemaDefinition(
        name = ValidAbstractImpl2.NAME,
        namespace = Constants.NAMESPACE,
        orgId = Constants.ORG_ID,
        tenantId = Constants.TENANT,
        type = Constants.TYPE,
        version = ValidAbstractImpl2.VERSION,
        description = ValidAbstractImpl2.DESCRIPTION,
        schemaType = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
public class ValidAbstractImpl2 extends ValidAbstractSchema {
    public static final String NAME = "VALID_ABSTRACT_IMPL_2";
    public static final String VERSION = "V0";
    public static final String DESCRIPTION = "Valid Abstract Impl 2";

    private PIIData piiData;
    @SchemaRef
    private TestObjectClass testObjectClass;
}

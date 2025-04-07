package com.grookage.leia.common.stubs.classes.abstracts.invalid;

import com.grookage.leia.common.stubs.PIIData;
import com.grookage.leia.common.stubs.classes.TestObjectClass;
import com.grookage.leia.common.stubs.classes.abstracts.valid.ValidAbstractImpl1;
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
        name = InvalidAbstractImpl1.NAME,
        namespace = Constants.NAMESPACE,
        orgId = Constants.ORG_ID,
        tenantId = Constants.TENANT,
        type = Constants.TYPE,
        version = InvalidAbstractImpl1.VERSION,
        description = InvalidAbstractImpl1.DESCRIPTION,
        schemaType = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
public class InvalidAbstractImpl1 extends InvalidAbstractSchema {
    public static final String NAME = "INVALID_ABSTRACT_IMPL_1";
    public static final String VERSION = "V0";
    public static final String DESCRIPTION = "InValid Abstract Impl 1";

    private PIIData piiData;
    @SchemaRef
    private TestObjectClass testObjectClass;
}

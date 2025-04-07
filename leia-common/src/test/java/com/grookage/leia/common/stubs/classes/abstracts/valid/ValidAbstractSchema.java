package com.grookage.leia.common.stubs.classes.abstracts.valid;

import com.grookage.leia.common.stubs.classes.abstracts.AbstractEntity;
import com.grookage.leia.common.utils.Constants;
import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.schema.SchemaType;
import com.grookage.leia.models.schema.SchemaValidationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@SchemaDefinition(
        name = ValidAbstractSchema.NAME,
        namespace = Constants.NAMESPACE,
        orgId = Constants.ORG_ID,
        tenantId = Constants.TENANT,
        type = Constants.TYPE,
        version = ValidAbstractSchema.VERSION,
        description = ValidAbstractSchema.DESCRIPTION,
        schemaType = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
public abstract class ValidAbstractSchema extends AbstractEntity {
    public static final String NAME = "VALID_ABSTRACT_SCHEMA";
    public static final String VERSION = "V0";
    public static final String DESCRIPTION = "Valid Abstract class Schema";
    String schemaName;
    String schemaDescription;
}

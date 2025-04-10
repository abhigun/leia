package com.grookage.leia.common.stubs.classes.abstracts;

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
        name = AbstractSchema.NAME,
        namespace = Constants.NAMESPACE,
        orgId = Constants.ORG_ID,
        tenantId = Constants.TENANT,
        type = Constants.TYPE,
        version = AbstractSchema.VERSION,
        description = AbstractSchema.DESCRIPTION,
        schemaType = SchemaType.JSON,
        validation = SchemaValidationType.MATCHING,
        tags = {"foxtrot", "audit"}
)
public abstract class AbstractSchema extends AbstractEntity {
    public static final String NAME = "ABSTRACT_SCHEMA";
    public static final String VERSION = "V0";
    public static final String DESCRIPTION = "Abstract class Schema";
    String schemaName;
    String schemaDescription;
}

package com.grookage.leia.models.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import com.grookage.leia.models.SchemaConstants;
import com.grookage.leia.models.annotations.SchemaDefinition;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Locale;

@Value
@Builder
@Jacksonized
public class SchemaReference {
    String orgId;
    String namespace;
    String tenantId;
    String name;
    String type;

    @JsonIgnore
    public static SchemaReference from(final SchemaDefinition schemaDefinition) {
        return SchemaReference.builder()
                .orgId(schemaDefinition.orgId())
                .namespace(schemaDefinition.namespace())
                .tenantId(schemaDefinition.tenantId())
                .name(schemaDefinition.name())
                .type(schemaDefinition.type())
                .build();
    }

    @JsonIgnore
    public String getReferenceId() {
        return Joiner.on(SchemaConstants.KEY_DELIMITER).join(orgId,
                namespace,
                tenantId,
                name
        ).toUpperCase(Locale.ROOT);
    }

    @Override
    public int hashCode() {
        return this.getReferenceId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final var thatKey = (SchemaReference) obj;
        return (thatKey.getReferenceId().equals(this.getReferenceId()));
    }
}

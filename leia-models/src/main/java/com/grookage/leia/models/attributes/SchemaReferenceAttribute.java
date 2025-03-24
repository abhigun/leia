package com.grookage.leia.models.attributes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.grookage.leia.models.qualifiers.QualifierInfo;
import com.grookage.leia.models.schema.SchemaReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class SchemaReferenceAttribute extends SchemaAttribute {
    private SchemaReference reference;

    public SchemaReferenceAttribute(final String name,
                                    final boolean optional,
                                    final Set<QualifierInfo> qualifiers) {
        super(DataType.REFERENCE, name, optional, qualifiers);
    }

    @Override
    public <T> T accept(SchemaAttributeAcceptor<T> attributeAcceptor) {
        return attributeAcceptor.accept(this);
    }
}

package com.grookage.leia.models.schema;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SchemaReference {
    String namespace;
    String name;
}

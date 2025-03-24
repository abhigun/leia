package com.grookage.leia.common.stubs;

import com.grookage.leia.models.annotations.SchemaDefinition;

@SchemaDefinition(
        namespace = "test",
        name = "nested_abstract_class",
        version = "v1"
)
public abstract class NestedAbstractClass {
    String name;
}

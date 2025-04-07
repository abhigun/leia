package com.grookage.leia.common.stubs.classes;

import com.grookage.leia.models.annotations.SchemaDefinition;

@SchemaDefinition(
        namespace = "testNamespace",
        name = "abstract_class_schema",
        version = "v1"
)
public abstract class NestedAbstractClass {
    String name;
}

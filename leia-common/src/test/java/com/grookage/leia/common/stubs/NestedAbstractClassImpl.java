package com.grookage.leia.common.stubs;

import com.grookage.leia.models.annotations.SchemaDefinition;

@SchemaDefinition(
        namespace = "test",
        name = "nested_abstract_class_impl",
        version = "v1"
)
public class NestedAbstractClassImpl extends NestedAbstractClass {
    String firstName;
}

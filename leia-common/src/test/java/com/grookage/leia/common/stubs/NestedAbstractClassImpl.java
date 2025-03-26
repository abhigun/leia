package com.grookage.leia.common.stubs;

import com.grookage.leia.models.annotations.SchemaDefinition;

@SchemaDefinition(
        namespace = "testNamespace",
        name = "abstract_class_impl_schema",
        version = "v1"
)
public class NestedAbstractClassImpl extends NestedAbstractClass {
    String firstName;
}

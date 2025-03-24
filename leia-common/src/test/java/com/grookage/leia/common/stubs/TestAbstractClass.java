package com.grookage.leia.common.stubs;

import com.grookage.leia.models.annotations.SchemaDefinition;

@SchemaDefinition(
        name = "test_abstract_class",
        namespace = "test",
        version = "v1"
)
public class TestAbstractClass {
    NestedAbstractClass nestedAbstractClass;
    NestedAbstractClassImpl nestedAbstractClassImpl;
}

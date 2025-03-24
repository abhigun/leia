package com.grookage.leia.common.stubs;

import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.annotations.SchemaReference;

@SchemaDefinition(
        name = "test_abstract_class",
        namespace = "test",
        version = "v1"
)
public class TestAbstractClass {
    @SchemaReference
    NestedAbstractClass nestedAbstractClass;
    @SchemaReference
    NestedAbstractClassImpl nestedAbstractClassImpl;
}

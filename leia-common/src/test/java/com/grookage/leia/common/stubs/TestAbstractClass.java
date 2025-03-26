package com.grookage.leia.common.stubs;

import com.grookage.leia.models.annotations.SchemaDefinition;
import com.grookage.leia.models.annotations.SchemaRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@SchemaDefinition(
        name = "test_abstract_class",
        namespace = "test",
        version = "v1"
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestAbstractClass {
    @SchemaRef
    NestedAbstractClass nestedAbstractClass;
    @SchemaRef
    NestedAbstractClassImpl nestedAbstractClassImpl;
    List<@SchemaRef NestedAbstractClassImpl> nestedAbstractClasses;
    List<String> strings;
}

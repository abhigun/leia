package com.grookage.leia.common.stubs;

import com.grookage.leia.models.annotations.attribute.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class TestPrimitiveStub {
    @Optional
    String name;
    int id;
    char c;
    short s;
    long l;
    byte b;
    double d;
    boolean bl;
    float f;
}

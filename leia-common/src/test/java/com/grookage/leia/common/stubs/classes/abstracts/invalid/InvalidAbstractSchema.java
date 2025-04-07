package com.grookage.leia.common.stubs.classes.abstracts.invalid;

import com.grookage.leia.common.stubs.classes.abstracts.AbstractEntity;
import com.grookage.leia.common.stubs.classes.abstracts.valid.ValidAbstractSchema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class InvalidAbstractSchema extends ValidAbstractSchema {
    private boolean valid;
}

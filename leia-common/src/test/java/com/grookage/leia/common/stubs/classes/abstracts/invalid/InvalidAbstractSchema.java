package com.grookage.leia.common.stubs.classes.abstracts.invalid;

import com.grookage.leia.common.stubs.classes.abstracts.AbstractSchema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class InvalidAbstractSchema extends AbstractSchema {
    private boolean valid;
}

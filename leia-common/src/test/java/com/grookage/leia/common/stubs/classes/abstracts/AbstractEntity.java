package com.grookage.leia.common.stubs.classes.abstracts;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractEntity {
    String id;
    String name;
    String domain;
}

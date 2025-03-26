package com.grookage.leia.common.validation;

import com.grookage.leia.common.violation.LeiaSchemaViolation;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
@Builder
public class ValidationResponse {
    List<LeiaSchemaViolation> violations;
    Set<Class<?>> classesToValidate;
}

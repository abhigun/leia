package com.grookage.leia.common.validation;

import com.grookage.leia.common.violation.LeiaSchemaViolation;
import com.grookage.leia.common.violation.LeiaSchemaViolationImpl;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Data
@Builder
@RequiredArgsConstructor
public class ValidationContext {
    @Getter
    private final List<LeiaSchemaViolation> violations = new ArrayList<>();
    private final LinkedList<Class<?>> klassPath = new LinkedList<>();
    private final Set<Class<?>> validationQueue = new HashSet<>();

    public void addViolation(final String message) {
        violations.add(new LeiaSchemaViolationImpl(message, null, klassPath.peekLast()));
    }

    public void addViolation(final String message,
                             final String path) {
        violations.add(new LeiaSchemaViolationImpl(message, path, klassPath.peekLast()));
    }

    public void addViolation(List<LeiaSchemaViolation> schemaViolations) {
        violations.addAll(schemaViolations);
    }

    public void pushClass(final Class<?> klass) {
        klassPath.addLast(klass);
    }

    public void popClass() {
        if (!klassPath.isEmpty()) {
            klassPath.removeLast();
        }
    }

    public void validateClass(final Class<?> klass) {
        validationQueue.add(klass);
    }
}

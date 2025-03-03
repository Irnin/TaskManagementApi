package com.project.employee_records.violation;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService<T> {
    private final Validator validator;

    public void validate(T object) {
        var violations = validator.validate(object);
        if(!violations.isEmpty())
            throw new ConstraintViolationException(violations);
    }
}
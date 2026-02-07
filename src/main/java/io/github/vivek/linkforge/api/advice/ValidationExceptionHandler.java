package io.github.vivek.linkforge.api.advice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@SuppressWarnings("unused")
public class ValidationExceptionHandler {

    public static final String VALIDATION_FAILED = "Validation failed";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .toList();

        return new ValidationErrorResponse(
                Instant.now(),
                VALIDATION_FAILED,
                errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolation(
            ConstraintViolationException ex) {

        var errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationError(
                        extractFieldName(violation.getPropertyPath().toString()),
                        violation.getMessage()
                ))
                .toList();

        return new ValidationErrorResponse(
                Instant.now(),
                VALIDATION_FAILED,
                errors
        );
    }

    private String extractFieldName(String path) {
        int lastDot = path.lastIndexOf('.');
        return lastDot != -1 ? path.substring(lastDot + 1) : path;
    }

    private ValidationError toValidationError(FieldError error) {
        return new ValidationError(
                error.getField(),
                error.getDefaultMessage()
        );
    }
}

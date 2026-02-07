package io.github.vivek.linkforge.api.advice;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        Instant timestamp,
        String message,
        List<ValidationError> errors
) {}

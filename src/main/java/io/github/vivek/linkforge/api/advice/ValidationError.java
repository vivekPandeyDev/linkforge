package io.github.vivek.linkforge.api.advice;

public record ValidationError(
        String field,
        String error
) {}

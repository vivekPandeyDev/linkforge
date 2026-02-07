package io.github.vivek.linkforge.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenRequest(
        @NotBlank(message = "longUrl must not be blank")
        @URL(
                regexp = "^(http|https)://.*",
                message = "longUrl must be a valid http or https URL"
        ) String longUrl
) {
}

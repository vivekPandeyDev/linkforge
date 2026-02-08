package io.github.vivek.linkforge.kafka;


import io.github.vivek.linkforge.utility.RequestContext;

import java.time.Instant;

public record RedirectEvent(
    String code,
    String originalUrl,
    Instant timestamp,
    String userAgent,
    String ip
) {
    static RedirectEvent from(String code, String url) {
        return new RedirectEvent(
            code,
            url,
            Instant.now(),
            RequestContext.userAgent(),
            RequestContext.ip()
        );
    }
}

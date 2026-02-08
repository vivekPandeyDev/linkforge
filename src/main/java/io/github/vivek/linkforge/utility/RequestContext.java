package io.github.vivek.linkforge.utility;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class RequestContext {

    private RequestContext() {}

    private static HttpServletRequest request() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    public static String userAgent() {
        HttpServletRequest req = request();
        return req != null ? req.getHeader("User-Agent") : "unknown";
    }

    public static String ip() {
        HttpServletRequest req = request();
        if (req == null) return "unknown";

        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return req.getRemoteAddr();
    }
}
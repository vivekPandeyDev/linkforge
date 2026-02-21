package io.github.vivek.linkforge.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    public static final String RATE_IP = "rate:ip:";
    private final RedissonClient redissonClient;

    private static final long REQUESTS = 20;
    private static final long WINDOW_MINUTES = 1;
    private static final long AUTO_CLEAN_IDLE_KEY_IN_MINUTES = 1;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();
        // Only apply to /api/**
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String ip = extractClientIp(request);

        RRateLimiter limiter = redissonClient.getRateLimiter(RATE_IP + ip);

        limiter.trySetRate(
                RateType.OVERALL,
                REQUESTS,
                Duration.ofMinutes(WINDOW_MINUTES),
                Duration.ofMinutes(AUTO_CLEAN_IDLE_KEY_IN_MINUTES)
        );
        log.debug("Remaining connection for the ip {}: {}", ip,limiter.availablePermits());

        if (!limiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(WINDOW_MINUTES));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
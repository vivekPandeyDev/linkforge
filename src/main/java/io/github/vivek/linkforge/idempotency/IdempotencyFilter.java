package io.github.vivek.linkforge.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    public static final String IDEMPOTENCY_VALIDATION_FAILED = "Idempotency key required";
    public static final String DUPLICATE_IDEMPOTENT_REQUEST = "Duplicate request";

    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(
                "POST".equalsIgnoreCase(request.getMethod()) &&
                        request.getRequestURI().startsWith("/api/")
        );
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        var key = request.getHeader(IDEMPOTENCY_KEY);

        if (key == null || key.isBlank()) {
            sendError(request, response, HttpStatus.BAD_REQUEST, IDEMPOTENCY_VALIDATION_FAILED, "Idempotency-Key header is required");
            return;
        }

        if (!idempotencyService.acquireLock(key)) {
            sendError(request, response, HttpStatus.CONFLICT, DUPLICATE_IDEMPOTENT_REQUEST, "This Idempotency-Key has already been used");
            return;
        }

        try {
            filterChain.doFilter(request, response);
            idempotencyService.markCompleted(key);
        } catch (Exception ex) {
            idempotencyService.release(key);
            throw ex;
        }
    }

    private void sendError(HttpServletRequest request,
                           HttpServletResponse response,
                           HttpStatus status,
                           String title,
                           String message) throws IOException {


        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(message);
        problem.setInstance(URI.create(request.getRequestURI()));

        response.setStatus(status.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), problem);
        response.flushBuffer(); // commit response
    }
}
package com.bertan.budgetplanner.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private Bucket createLoginBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(5)
                        .refillGreedy(5, Duration.ofMinutes(1)))
                .build();
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String userIp) {
        return buckets.computeIfAbsent(userIp, k -> createLoginBucket());
    }

    private String resolveUserIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        } else {
            return request.getRemoteAddr();
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isLoginRequest = Objects.equals(request.getMethod(), "POST")
                && request.getRequestURI().equals("/api/v1/auth/login");

        if (isLoginRequest) {
            String userIp = resolveUserIp(request);
            Bucket bucket = resolveBucket(userIp);

            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Too many login attempts. Please try again later.\"}");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

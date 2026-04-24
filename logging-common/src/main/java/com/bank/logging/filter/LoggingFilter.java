













package com.bank.logging.filter;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    @Value("${spring.application.name:unknown-service}")
    private String applicationName;

    // Standard MDC keys
    public static final String SERVICE    = "service";
    public static final String METHOD     = "method";
    public static final String PATH       = "path";
    public static final String STATUS     = "status";
    public static final String DURATION_MS = "durationMs";
    public static final String TRACE_ID   = "traceId";
    public static final String CLIENT_IP  = "clientIp";
    public static final String ENV        = "env";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        
        // Micrometer Tracing should have already initialized the traceId in MDC.
        // We ensure service name is present.
        MDC.put(SERVICE, applicationName);
        MDC.put(METHOD, request.getMethod());
        MDC.put(PATH, request.getRequestURI());
        MDC.put(ENV, System.getenv().getOrDefault("APP_ENV", "production"));

        // Extract client IP — check X-Forwarded-For first (set by proxies/load balancers)
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp != null && !clientIp.isBlank()) {
            // X-Forwarded-For can be a comma-separated list; take the first (original client)
            clientIp = clientIp.split(",")[0].trim();
        } else {
            clientIp = request.getRemoteAddr();
        }
        MDC.put(CLIENT_IP, clientIp);

        // If traceId is missing in MDC (shouldn't happen with Micrometer), get it from tracer
        if (MDC.get("traceId") == null && tracer.currentSpan() != null) {
            MDC.put(TRACE_ID, tracer.currentSpan().context().traceId());
        }

        try {
            log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
            
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            MDC.put(STATUS, String.valueOf(response.getStatus()));
            MDC.put(DURATION_MS, String.valueOf(duration));

            log.info("Request completed: {} {}  {} in {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            // Clear custom MDC fields
            MDC.remove(SERVICE);
            MDC.remove(METHOD);
            MDC.remove(PATH);
            MDC.remove(STATUS);
            MDC.remove(DURATION_MS);
            MDC.remove(CLIENT_IP);
            MDC.remove(ENV);
        }
    }
}
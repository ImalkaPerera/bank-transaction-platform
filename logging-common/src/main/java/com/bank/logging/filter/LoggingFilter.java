













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

    // Nested MDC keys for better ES structure
    public static final String SERVICE          = "service";
    public static final String HTTP_METHOD      = "http.method";
    public static final String HTTP_PATH        = "http.path";
    public static final String HTTP_STATUS      = "http.status";
    public static final String HTTP_DURATION    = "http.responseTimeMs";
    public static final String HTTP_CLIENT_IP   = "http.clientIp";
    public static final String TRACE_ID         = "traceId";
    public static final String ENV              = "env";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        MDC.put("request.start_time", String.valueOf(startTime));

        MDC.put(SERVICE, applicationName);
        MDC.put(HTTP_METHOD, request.getMethod());
        MDC.put(ENV, System.getenv().getOrDefault("APP_ENV", "production"));

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp != null && !clientIp.isEmpty()) {
            clientIp = clientIp.split(",")[0].trim();
        } else {
            clientIp = request.getRemoteAddr();
        }
        MDC.put(HTTP_CLIENT_IP, clientIp);

        if (MDC.get("traceId") == null && tracer.currentSpan() != null) {
            MDC.put(TRACE_ID, tracer.currentSpan().context().traceId());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            // Capture normalized path (e.g., /accounts/{id}) if available
            String pattern = (String) request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern");
            String path = (pattern != null) ? pattern : request.getRequestURI();

            MDC.put(HTTP_PATH, path);
            MDC.put(HTTP_STATUS, String.valueOf(response.getStatus()));
            MDC.put(HTTP_DURATION, String.valueOf(duration));

            // Single log entry per request with all metadata
            log.info("Request completed: {} {} - Status: {} in {}ms",
                    request.getMethod(),
                    path,
                    response.getStatus(),
                    duration);

            MDC.clear(); // Clear all to avoid leakage
        }
    }
}
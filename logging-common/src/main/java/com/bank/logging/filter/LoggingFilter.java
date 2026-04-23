













// Filter that logs details about every HTTP request and response.
package com.bank.logging.filter;

import com.bank.logging.util.MDCUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Value("${spring.application.name:unknown-service}")
    private String applicationName;

    // This method runs for every HTTP request
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            // Generate a unique traceId for this request
            String traceId = MDCUtil.generateTraceId();

            // Set the service name in the logging context
            MDCUtil.set(MDCUtil.SERVICE, applicationName);

            // Add method, path, and IP to the logging context
            MDCUtil.set(MDCUtil.METHOD, request.getMethod());
            MDCUtil.set(MDCUtil.PATH,   request.getRequestURI());
            MDCUtil.set(MDCUtil.IP,     getClientIp(request));

            // Log the incoming request
            log.info("Incoming request: {} {}",
                    request.getMethod(),
                    request.getRequestURI());

            // Continue with the rest of the filter chain
            filterChain.doFilter(request, response);

            // Log the response status and duration
            long duration = System.currentTimeMillis() - startTime;
            MDCUtil.set(MDCUtil.STATUS,      String.valueOf(response.getStatus()));
            MDCUtil.set(MDCUtil.DURATION_MS, String.valueOf(duration));

            log.info("Request completed: {} {}  {} in {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

        } finally {
            // Always clear the logging context at the end
            MDCUtil.clear();
        }
    }

    // Helper to get the client IP address
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
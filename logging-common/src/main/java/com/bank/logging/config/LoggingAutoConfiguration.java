




package com.bank.logging.config;

import com.bank.logging.exception.GlobalExceptionHandler;
import com.bank.logging.filter.LoggingFilter;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// This class sets up logging for your Spring Boot app.
// It adds a filter to log request details and a handler to log errors.
@Configuration
public class LoggingAutoConfiguration {

    // Adds a filter that logs info about every HTTP request.
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public LoggingFilter loggingFilter(Tracer tracer) {
        return new LoggingFilter(tracer);
    }

    // Adds a handler that logs uncaught exceptions as errors.
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
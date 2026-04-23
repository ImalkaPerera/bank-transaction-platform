




package com.bank.logging.config;

import com.bank.logging.exception.GlobalExceptionHandler;
import com.bank.logging.filter.LoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// This class sets up logging for your Spring Boot app.
// It adds a filter to log request details and a handler to log errors.
@Configuration
public class LoggingAutoConfiguration {

    // Adds a filter that logs info about every HTTP request.
    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    // Adds a handler that logs uncaught exceptions as errors.
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
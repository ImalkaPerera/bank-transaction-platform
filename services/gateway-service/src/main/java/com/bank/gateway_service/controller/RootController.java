package com.bank.gateway_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

/**
 * This controller handles requests to the root path ("/").
 * It prevents automated bots and health checks from triggering 404 errors in the logs.
 */
@Slf4j
@RestController
public class RootController {

    @GetMapping("/")
    public Mono<String> healthCheck() {
        log.debug("Root path accessed (likely a health check or bot scan).");
        return Mono.just("Bank Gateway: System is healthy and operational.");
    }
}

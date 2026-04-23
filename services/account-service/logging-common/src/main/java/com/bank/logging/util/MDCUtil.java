







// Utility class for managing logging context (MDC) values like traceId, method, etc.
package com.bank.logging.util;

import org.slf4j.MDC;
import java.util.UUID;

public class MDCUtil {

    // Keys for common MDC fields
    public static final String TRACE_ID    = "traceId";
    public static final String SERVICE     = "service";
    public static final String METHOD      = "method";
    public static final String PATH        = "path";
    public static final String STATUS      = "status";
    public static final String DURATION_MS = "durationMs";
    public static final String IP          = "ip";

    // Set a value in the MDC
    public static void set(String key, String value) {
        MDC.put(key, value);
    }

    // Generate and set a unique traceId for the current request
    public static String generateTraceId() {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);
        return traceId;
    }

    // Clear all MDC values (should be called at the end of a request)
    public static void clear() {
        MDC.clear();
    }

    // Get a value from the MDC
    public static String get(String key) {
        return MDC.get(key);
    }
}
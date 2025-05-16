package dev.park.dailynews.common;

import dev.park.dailynews.model.SessionContext;
import jakarta.servlet.http.HttpServletRequest;

public class SessionUtils {

    public static SessionContext getSessionInfo(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }

        String userAgent = request.getHeader("User-Agent");

        return SessionContext.builder()
                .ip(ip)
                .userAgent(userAgent)
                .build();
    }
}

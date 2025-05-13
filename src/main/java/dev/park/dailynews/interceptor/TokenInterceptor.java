package dev.park.dailynews.interceptor;

import dev.park.dailynews.exception.CookieNotExistException;
import dev.park.dailynews.exception.NullCookieException;
import dev.park.dailynews.exception.UnAuthorized;
import dev.park.dailynews.infra.auth.jwt.TokenValidator;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = extractToken(request);
        SessionContext session = getSessionInfo(request);
        if (!tokenValidator.validToken(accessToken, session)) {
            throw new UnAuthorized();
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private String extractToken(HttpServletRequest request) {

        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(CookieNotExistException::new);

        String accessToken = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("DN_AUT"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(NullCookieException::new);

        return accessToken;
    }

    private SessionContext getSessionInfo(HttpServletRequest request) {
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

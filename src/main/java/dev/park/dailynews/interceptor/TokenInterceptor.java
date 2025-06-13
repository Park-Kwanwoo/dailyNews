package dev.park.dailynews.interceptor;

import dev.park.dailynews.common.CookieUtils;
import dev.park.dailynews.exception.UnAuthorized;
import dev.park.dailynews.infra.auth.jwt.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = request.getHeader("Authorization");

        if (accessToken == null || !tokenValidator.validToken(accessToken)) {
            throw new UnAuthorized();
        }

        if (request.getRequestURI().equals("/social/logout")) {
            request.setAttribute("accessToken", accessToken);
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

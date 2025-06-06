package dev.park.dailynews.interceptor;

import dev.park.dailynews.common.SessionUtils;
import dev.park.dailynews.exception.UnAuthorized;
import dev.park.dailynews.infra.auth.jwt.TokenValidator;
import dev.park.dailynews.model.SessionContext;
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
        SessionContext session = SessionUtils.getSessionInfo(request);

        if (accessToken == null || !tokenValidator.validToken(accessToken, session)) {
            throw new UnAuthorized();
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

package dev.park.dailynews.config;

import dev.park.dailynews.common.CookieUtils;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.LoginUserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class UserInfoResolver implements HandlerMethodArgumentResolver {

    private final JwtUtils jwtUtils;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginUserContext.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = request.getHeader("Authorization");
        String email = jwtUtils.extractSubject(accessToken);

        return new LoginUserContext(email);
    }
}

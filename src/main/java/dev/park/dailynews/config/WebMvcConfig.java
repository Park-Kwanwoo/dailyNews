package dev.park.dailynews.config;

import dev.park.dailynews.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;
    private final UserInfoResolver userInfoResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns(
                        "/news",
                        "/news/{newsId}",
                        "/register/subject",
                        "/subject",
                        "/social/logout",
                        "/social/info"
                );
    }
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(resolvers());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://dailynews.p-e.kr")
                .allowedHeaders("*")
                .allowedMethods("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }

    private List<HandlerMethodArgumentResolver> resolvers() {
        return List.of(userInfoResolver);
    }
}

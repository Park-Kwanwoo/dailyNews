package dev.park.dailynews.infra.social;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "social.kakao")
@RequiredArgsConstructor
@Getter
public class KakaoProperties {

    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    private final String tokenUrl;
    private final String userInfoUrl;
}

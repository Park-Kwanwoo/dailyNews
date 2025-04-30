package dev.park.dailynews.oauth.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
@RequiredArgsConstructor
@Getter
public class KakaoProperties {

    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
}

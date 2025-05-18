package dev.park.dailynews.infra.social;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("social.naver")
@AllArgsConstructor
@Getter
public class NaverProperties {

    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;
    private final String userInfoUrl;
}


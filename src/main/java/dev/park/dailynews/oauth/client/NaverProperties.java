package dev.park.dailynews.oauth.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth.naver")
@AllArgsConstructor
@Getter
public class NaverProperties {

    private final String grantType;
    private final String clientId;
    private final String clientSecret;

}

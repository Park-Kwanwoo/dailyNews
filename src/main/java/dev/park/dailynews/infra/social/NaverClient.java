package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.NaverToken;
import dev.park.dailynews.domain.social.NaverUserInfo;
import dev.park.dailynews.domain.social.SocialUserInfo;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class NaverClient implements SocialClient {

    private final NaverProperties naverProperties;
    private final RestTemplate rt;

    private final static String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private final static String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

    @Override
    public SocialProvider socialProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    public String requestAccessToken(SocialLoginParams params) {

        HttpEntity<MultiValueMap<String, String>> request = makeTokenRequest(params.getCode());

        NaverToken naverToken = rt.postForObject(
                TOKEN_URL,
                request,
                NaverToken.class
                );

        return naverToken.accessToken();
    }

    @Override
    public SocialUserInfo requestUserInfo(String accessToken) {

        HttpEntity<MultiValueMap<String, String>> request = makeUserInfoRequest(accessToken);

        NaverUserInfo naverUserInfo = rt.postForObject(
                USER_INFO_URL,
                request,
                NaverUserInfo.class
        );
        return naverUserInfo;
    }

    private HttpEntity<MultiValueMap<String, String>> makeTokenRequest(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", naverProperties.getGrantType());
        body.add("client_id", naverProperties.getClientId());
        body.add("client_secret", naverProperties.getClientSecret());
        body.add("state", "test");
        body.add("code", code);

        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> makeUserInfoRequest(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        log.info("accessToken = {}", accessToken);

        return new HttpEntity<>(headers);
    }
}

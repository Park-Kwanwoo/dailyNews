package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.NaverToken;
import dev.park.dailynews.domain.social.NaverUserInfo;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.domain.social.SocialUserInfo;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.model.SocialUserInfoContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static dev.park.dailynews.domain.social.SocialProvider.NAVER;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverClient implements SocialClient {

    private final NaverProperties naverProperties;
    private final RestTemplate rt;

    @Override
    public SocialProvider socialProvider() {
        return NAVER;
    }

    @Override
    public String requestAccessToken(SocialLoginParams params) {

        HttpEntity<MultiValueMap<String, String>> request = tokenRequest(params.getCode());

        NaverToken naverToken = rt.postForObject(
                naverProperties.getTokenUrl(),
                request,
                NaverToken.class
                );

        return naverToken.accessToken();
    }

    @Override
    public SocialUserInfoContext requestUserInfo(String accessToken) {

        HttpEntity<MultiValueMap<String, String>> request = userInfoRequest(accessToken);

        NaverUserInfo naverUserInfo = rt.postForObject(
                naverProperties.getUserInfoUrl(),
                request,
                NaverUserInfo.class
        );
        return SocialUserInfoContext.builder()
                .id(naverUserInfo.getId())
                .email(naverUserInfo.getEmail())
                .nickname(naverUserInfo.getNickname())
                .provider(NAVER)
                .socialToken(accessToken)
                .build();
    }

    @Override
    public void logout(SocialLogoutParams params) {

        HttpEntity<MultiValueMap<String, String>> logoutRequest = logoutRequest(params);

        Object o = rt.postForObject(
                naverProperties.getTokenUrl(),
                logoutRequest,
                Object.class
        );

        log.info("{}", o);

    }

    private HttpEntity<MultiValueMap<String, String>> tokenRequest(String code) {

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

    private HttpEntity<MultiValueMap<String, String>> userInfoRequest(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        return new HttpEntity<>(headers);
    }

    private HttpEntity<MultiValueMap<String, String>> logoutRequest(SocialLogoutParams params) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "delete");
        body.add("client_id", naverProperties.getClientId());
        body.add("client_secret", naverProperties.getClientSecret());
        body.add("access_token", params.getToken());
        body.add("service_provider", params.getProvider().toString());

        return new HttpEntity<>(body, headers);
    }
}

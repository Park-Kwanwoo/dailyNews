package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.KakaoToken;
import dev.park.dailynews.domain.social.KakaoUserInfo;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.model.SocialUserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoClient implements SocialClient {

    private final KakaoProperties kakaoProperties;

    private final RestTemplate rt;

    @Override
    public SocialProvider socialProvider() {
        return KAKAO;
    }

    @Override
    public String requestAccessToken(SocialLoginParams params) {

        HttpEntity<MultiValueMap<String, String>> tokenRequest = tokenRequest(params.getCode());

        KakaoToken kakaoToken = rt.postForObject(
                kakaoProperties.getTokenUrl(),
                tokenRequest,
                KakaoToken.class
        );

        return kakaoToken.accessToken();
    }

    @Override
    public SocialUserInfoContext requestUserInfo(String accessToken) {

        HttpEntity<MultiValueMap<String, String>> userInfoRequest = userInfoRequest(accessToken);

        KakaoUserInfo kakaoUserInfo = rt.postForObject(
                kakaoProperties.getUserInfoUrl(),
                userInfoRequest,
                KakaoUserInfo.class
        );

        return SocialUserInfoContext.builder()
                .email(kakaoUserInfo.getEmail())
                .nickname(kakaoUserInfo.getNickname())
                .provider(KAKAO)
                .socialToken(accessToken)
                .build();
    }

    @Override
    public void logout(SocialLogoutParams params) {

        HttpEntity<MultiValueMap<String, String>> logoutRequest = logoutRequest(params.getToken());

        String id = rt.postForObject(
                kakaoProperties.getLogoutUrl(),
                logoutRequest,
                String.class
        );

        log.info("{}", id);
    }

    private HttpEntity<MultiValueMap<String, String>> tokenRequest(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", kakaoProperties.getGrantType());
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("client_secret", kakaoProperties.getClientSecret());
        body.add("code", code);

        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> userInfoRequest(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer " + accessToken);
        headers.add(CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile.nickname\"]");

        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> logoutRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        headers.add(AUTHORIZATION, "Bearer " + token);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        return new HttpEntity<>(body, headers);
    }
}

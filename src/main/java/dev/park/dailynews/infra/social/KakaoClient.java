package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.KakaoToken;
import dev.park.dailynews.domain.social.KakaoUserInfo;
import dev.park.dailynews.domain.social.SocialUserInfo;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
@Slf4j
public class KakaoClient implements SocialClient {

    private final KakaoProperties kakaoProperties;
    private final static String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final static String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate rt;

    @Override
    public SocialProvider socialProvider() {
        return SocialProvider.KAKAO;
    }
    @Override
    public String requestAccessToken(SocialLoginParams params) {

        HttpEntity<MultiValueMap<String, String>> request = tokenRequest(params.getCode());

        KakaoToken kakaoToken = rt.postForObject(
                TOKEN_URL,
                request,
                KakaoToken.class
        );

        return kakaoToken.accessToken();
    }

    @Override
    public SocialUserInfo requestUserInfo(String accessToken) {

        HttpEntity<MultiValueMap<String, String>> request = userInfoRequest(accessToken);

        KakaoUserInfo kakaoUserInfo = rt.postForObject(
                USER_INFO_URL,
                request,
                KakaoUserInfo.class
        );

        return kakaoUserInfo;
    }

    private HttpEntity<MultiValueMap<String, String>> tokenRequest(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", kakaoProperties.getGrantType());
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("client_secret", kakaoProperties.getClientSecret());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return request;
    }

    private HttpEntity<MultiValueMap<String, String>> userInfoRequest(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        log.info("accessToken = {}", accessToken);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile.nickname\"]");

        return new HttpEntity<>(body, headers);
    }
}

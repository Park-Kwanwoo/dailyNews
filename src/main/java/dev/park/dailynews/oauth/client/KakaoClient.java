package dev.park.dailynews.oauth.client;

import dev.park.dailynews.oauth.domain.KakaoUserInfo;
import dev.park.dailynews.oauth.domain.OAuth2UserInfo;
import dev.park.dailynews.oauth.domain.OAuthProvider;
import dev.park.dailynews.oauth.domain.KakaoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoClient implements OAuthClient {

    @Value("${oauth.kakao.grant-type}")
    private String grant_type;

    @Value("${oauth.kakao.client-id}")
    private String client_id;

    @Value("${oauth.kakao.client-secret}")
    private String client_secret;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirect_uri;

    private final static String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final static String USER_IFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate rt;

    @Override
    public OAuthProvider oauthProvider() {
        return OAuthProvider.KAKAO;
    }
    @Override
    public String requestAccessToken(String code) {

        HttpEntity<MultiValueMap<String, String>> request = tokenRequest(code);

        KakaoToken kakaoToken = rt.postForObject(
                TOKEN_URL,
                request,
                KakaoToken.class
        );

        return kakaoToken.accessToken();
    }

    @Override
    public OAuth2UserInfo requestUserInfo(String accessToken) {

        HttpEntity<MultiValueMap<String, String>> request = userInfoRequest(accessToken);

        KakaoUserInfo kakaoUserInfo = rt.postForObject(
                USER_IFO_URL,
                request,
                KakaoUserInfo.class
        );

        return kakaoUserInfo;
    }

    private HttpEntity<MultiValueMap<String, String>> tokenRequest(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", grant_type);
        body.add("client_id", client_id);
        body.add("redirect_uri", redirect_uri);
        body.add("client_secret", client_secret);
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

package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.NaverToken;
import dev.park.dailynews.domain.social.NaverUserInfo;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.domain.social.SocialUserInfo;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Getter
@Service
@RequiredArgsConstructor
public class NaverClient implements SocialClient {

    private final NaverProperties naverProperties;
    private final RestTemplate rt;

    @Override
    public SocialProvider socialProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    public String requestAccessToken(SocialLoginParams params) {

        HttpEntity<MultiValueMap<String, String>> request = makeTokenRequest(params.getCode());

        NaverToken naverToken = rt.postForObject(
                naverProperties.getTokenUrl(),
                request,
                NaverToken.class
                );

        return naverToken.accessToken();
    }

    @Override
    public SocialUserInfo requestUserInfo(String accessToken) {

        HttpEntity<MultiValueMap<String, String>> request = makeUserInfoRequest(accessToken);

        NaverUserInfo naverUserInfo = rt.postForObject(
                naverProperties.getUserInfoUrl(),
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

        return new HttpEntity<>(headers);
    }
}

package dev.park.dailynews.service;

import dev.park.dailynews.oauth.client.OAuthClient;
import dev.park.dailynews.oauth.domain.OAuth2UserInfo;
import dev.park.dailynews.oauth.domain.OAuthProvider;
import dev.park.dailynews.oauth.response.OAuthLoginParams;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthClientService {

    private final Map<OAuthProvider, OAuthClient> clients;

    public OAuthClientService(List<OAuthClient> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toUnmodifiableMap(OAuthClient::oauthProvider, Function.identity()));
    }

    public OAuth2UserInfo request(OAuthLoginParams params) {

        OAuthClient client = clients.get(params.getOAuthProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestUserInfo(accessToken);
    }
}

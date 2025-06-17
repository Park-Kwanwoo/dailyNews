package dev.park.dailynews.service;

import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.infra.social.SocialClient;
import dev.park.dailynews.model.SocialUserInfoContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialClientService {

    private final Map<SocialProvider, SocialClient> clients;

    public SocialClientService(List<SocialClient> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toUnmodifiableMap(SocialClient::socialProvider, Function.identity()));
    }

    public SocialUserInfoContext getUserInfo(SocialLoginParams params) {

        SocialClient client = clients.get(params.getSocialProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestUserInfo(accessToken);
    }

    public void logout(SocialLogoutParams params) {

        SocialClient client = clients.get(params.getProvider());
        client.logout(params);
    }
}

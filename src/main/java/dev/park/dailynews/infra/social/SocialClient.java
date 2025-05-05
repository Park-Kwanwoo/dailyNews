package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.SocialUserInfo;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;

public interface SocialClient {

    SocialProvider socialProvider();
    String requestAccessToken(SocialLoginParams response);

    SocialUserInfo requestUserInfo(String accessToken);
}

package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.model.SocialUserInfoContext;

public interface SocialClient {

    SocialProvider socialProvider();
    String requestAccessToken(SocialLoginParams params);

    SocialUserInfoContext requestUserInfo(String accessToken);

    void logout(SocialLogoutParams params);
}

package dev.park.dailynews.oauth.client;

import dev.park.dailynews.oauth.domain.OAuth2UserInfo;
import dev.park.dailynews.oauth.domain.OAuthProvider;

public interface OAuthClient {

    OAuthProvider oauthProvider();
    String requestAccessToken(String code);

    OAuth2UserInfo requestUserInfo(String accessToken);
}

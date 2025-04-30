package dev.park.dailynews.oauth.client;

import dev.park.dailynews.oauth.domain.OAuth2UserInfo;
import dev.park.dailynews.oauth.domain.OAuthProvider;
import dev.park.dailynews.oauth.response.OAuthLoginParams;

public interface OAuthClient {

    OAuthProvider oauthProvider();
    String requestAccessToken(OAuthLoginParams response);

    OAuth2UserInfo requestUserInfo(String accessToken);
}

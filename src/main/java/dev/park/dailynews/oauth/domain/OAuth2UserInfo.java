package dev.park.dailynews.oauth.domain;

public interface OAuth2UserInfo {

    OAuthProvider getProvider();
    String getEmail();

    String getNickname();
}

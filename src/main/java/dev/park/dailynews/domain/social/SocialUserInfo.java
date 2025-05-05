package dev.park.dailynews.domain.social;

public interface SocialUserInfo {

    SocialProvider getProvider();
    String getEmail();

    String getNickname();
}

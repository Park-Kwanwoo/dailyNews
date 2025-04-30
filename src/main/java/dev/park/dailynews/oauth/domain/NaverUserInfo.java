package dev.park.dailynews.oauth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverUserInfo(Response response) implements OAuth2UserInfo{

    @JsonIgnoreProperties
    record Response(String email, String nickname) {
    }
    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public String getNickname() {
        return response.nickname;
    }
}

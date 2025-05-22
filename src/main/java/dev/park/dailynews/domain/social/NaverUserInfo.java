package dev.park.dailynews.domain.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record NaverUserInfo(Response response) implements SocialUserInfo {

    @JsonIgnoreProperties
    public record Response(String email, String nickname) {
    }
    @Override
    public SocialProvider getProvider() {
        return SocialProvider.NAVER;
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

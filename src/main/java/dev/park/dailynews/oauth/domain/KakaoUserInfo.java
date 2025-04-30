package dev.park.dailynews.oauth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfo(@JsonProperty("kakao_account") KakaoAccount kakaoAccount) implements OAuth2UserInfo {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record KakaoAccount(String email, Profile profile) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Profile(String nickname) {
        }
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

    @Override
    public String getNickname() {
        return kakaoAccount.profile.nickname;
    }
}
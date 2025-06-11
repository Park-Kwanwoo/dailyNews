package dev.park.dailynews.domain.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfo(
        @JsonProperty("id") String id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount) implements SocialUserInfo {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(String email, Profile profile) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Profile(String nickname) {
        }
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

    @Override
    public String getNickname() {
        return kakaoAccount.profile.nickname;
    }

    @Override
    public String getId() {
        return id;
    }
}
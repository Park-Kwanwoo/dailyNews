package dev.park.dailynews.oauth.response;

import dev.park.dailynews.oauth.domain.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginParams implements OAuthLoginParams {

    private String code;

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String getCode() {
        return this.code;
    }

}

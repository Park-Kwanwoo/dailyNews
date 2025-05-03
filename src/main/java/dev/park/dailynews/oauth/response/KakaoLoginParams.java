package dev.park.dailynews.oauth.response;

import dev.park.dailynews.oauth.domain.OAuthProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginParams implements OAuthLoginParams {

    @NotBlank
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

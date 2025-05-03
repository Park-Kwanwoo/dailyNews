package dev.park.dailynews.oauth.response;

import dev.park.dailynews.oauth.domain.OAuthProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginParams implements OAuthLoginParams {

    @NotBlank
    private String code;

    @NotBlank
    private String state;

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}

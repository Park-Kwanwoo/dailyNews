package dev.park.dailynews.dto.response;

import dev.park.dailynews.domain.user.AuthToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

    private final String accessToken;
    private final String refreshToken;

    public static TokenResponse from(AuthToken token) {
        return new TokenResponse(token.getAccessToken(), token.getRefreshToken());
    }
}

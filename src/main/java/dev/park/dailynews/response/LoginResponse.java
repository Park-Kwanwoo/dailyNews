package dev.park.dailynews.response;

import dev.park.dailynews.oauth.domain.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class LoginResponse {

    private final String accessToken;
    private final String email;
    private final OAuthProvider oAuthProvider;
}

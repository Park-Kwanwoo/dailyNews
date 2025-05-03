package dev.park.dailynews.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.park.dailynews.oauth.domain.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;

@Getter
@RequiredArgsConstructor
@Builder
public class LoginResponse {

    private final String email;
    private final OAuthProvider oAuthProvider;

    @JsonIgnore
    private final String uuid;

    @JsonIgnore
    private final String accessToken;
}

package dev.park.dailynews.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class LoginResponse {

    private final String email;

    @JsonIgnore
    private final String accessToken;

    @JsonIgnore
    private final String userSession;
}

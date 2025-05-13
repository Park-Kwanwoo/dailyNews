package dev.park.dailynews.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenContext {

    private final String accessToken;
    private final String refreshToken;
    private final String ip;
    private String userAgent;
    private String email;
}



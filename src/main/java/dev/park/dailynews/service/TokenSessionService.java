package dev.park.dailynews.service;

import dev.park.dailynews.common.session.SessionCrypto;
import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.dto.request.UserSessionRequest;
import dev.park.dailynews.dto.response.LoginResponse;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.model.SessionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenSessionService {

    private final TokenService tokenService;
    private final SessionService sessionService;

    public LoginResponse issueSessionAndToken(UserContext userContext, UserSessionRequest userSessionRequest) throws Exception {
        AuthToken token = tokenService.findOrCreateToken(userContext);
        String sessionId = sessionService.findOrCreateSession(userContext, userSessionRequest);

        return LoginResponse.builder()
                .accessToken(token.getAccessToken())
                .nickname(userContext.getNickname())
                .sessionId(sessionId)
                .email(userContext.getEmail())
                .build();
    }
}

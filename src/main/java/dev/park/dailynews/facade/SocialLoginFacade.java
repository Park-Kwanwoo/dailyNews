package dev.park.dailynews.facade;

import dev.park.dailynews.common.session.SessionCrypto;
import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.dto.request.UserSessionRequest;
import dev.park.dailynews.dto.response.LoginResponse;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.service.SessionService;
import dev.park.dailynews.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialLoginFacade {

    private final TokenService tokenService;
    private final SessionService sessionService;
    private final SessionCrypto sessionCrypto;

    public LoginResponse issueSessionAndToken(UserContext userContext, UserSessionRequest userSessionRequest) throws Exception {
        AuthToken token = tokenService.createToken(userContext);
        SessionContext sessionContext = sessionService.findOrCreate(userContext, userSessionRequest);
        String encodedSession = sessionCrypto.encode(sessionContext);

        return LoginResponse.builder()
                .accessToken(token.getAccessToken())
                .userSession(encodedSession)
                .email(userContext.getEmail())
                .build();
    }
}

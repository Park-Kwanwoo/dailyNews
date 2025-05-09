package dev.park.dailynews.service;

import dev.park.dailynews.common.session.SessionCrypto;
import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.dto.request.UserSessionRequest;
import dev.park.dailynews.dto.response.LoginResponse;
import dev.park.dailynews.model.SessionId;
import dev.park.dailynews.model.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TokenSessionServiceTest {

    @InjectMocks
    private TokenSessionService tokenSessionService;

    @Mock
    private TokenService tokenService;

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionCrypto sessionCrypto;

    @Nested
    @DisplayName("성공 케이스")
    class SUCCESS_CASE {

        @Test
        @DisplayName("토큰 세션 발급 성공")
        void CREATE_TOKEN_SESSION() throws Exception {

            UserContext userContext = new UserContext("social@auth.com", "테스터", "test-uuid");
            UserSessionRequest userSessionRequest = new UserSessionRequest("test-Ip", "test-User-Agent");

            // given
            AuthToken fakeToken = AuthToken.builder()
                    .uuid("test-uuid")
                    .accessToken("access-token")
                    .build();

            given(tokenService.findOrCreateToken(userContext)).willReturn(fakeToken);
            given(sessionService.findOrCreateSession(userContext, userSessionRequest)).willReturn("fakeSessionId");

            // when
            LoginResponse result = tokenSessionService.issueSessionAndToken(userContext, userSessionRequest);

            // then
            assertEquals("social@auth.com", result.getEmail());
            assertEquals("테스터", result.getNickname());
            assertEquals("access-token", result.getAccessToken());
            assertEquals("fakeSessionId", result.getSessionId());
        }

    }
}

package dev.park.dailynews.service;

import dev.park.dailynews.common.session.SessionCrypto;
import dev.park.dailynews.domain.user.UserSession;
import dev.park.dailynews.dto.request.UserSessionRequest;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisSessionRepository;
import dev.park.dailynews.repository.RedisTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private RedisSessionRepository redisSessionRepository;

    @Mock
    private SessionCrypto crypto;

    @Nested
    @DisplayName("성공 케이스")
    class SUCCESS_CASE {

        @Test
        @DisplayName("세션 반환 성공")
        void RETURN_SESSION() throws Exception {

            UserContext userContext = new UserContext("social@test.com", "테스터", "test-uuid");
            UserSessionRequest userSessionRequest = new UserSessionRequest("test-Ip", "test-User-Agent");

            // given
            given(redisSessionRepository.findByEmail("social@test.com")).willReturn(Optional.empty());
            given(redisSessionRepository.save(any(UserSession.class))).willAnswer(i -> i.getArgument(0));
            given(crypto.encode(any(UserSession.class))).willReturn("encrypted-sessionId");

            // when
            String result = sessionService.findOrCreateSession(userContext, userSessionRequest);

            // then
            Assertions.assertEquals("encrypted-sessionId", result);
        }
    }
}

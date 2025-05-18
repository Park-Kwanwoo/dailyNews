package dev.park.dailynews.service;

import dev.park.dailynews.dto.response.TokenResponse;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.model.TokenContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RedisTokenRepository redisTokenRepository;

    @Nested
    @DisplayName("성공 케이스")
    class SUCCESS_CASE {

        @Test
        @DisplayName("토큰 발급 성공")
        void GENERATE_TOKEN() {

            UserContext userContext = new UserContext("social@test.com", "test-uuid");
            SessionContext sessionContext = new SessionContext("127.0.0.1", "Mozilla/5.0");

            // given
            given(redisTokenRepository.findByEmail("social@test.com")).willReturn(Optional.empty());
            given(jwtUtils.generateAccessToken(userContext)).willReturn("access-token");
            given(jwtUtils.generateRefreshToken(userContext)).willReturn("refresh-token");
            given(redisTokenRepository.save(any(AuthToken.class))).willAnswer(i -> i.getArgument(0));

            // when
            TokenResponse result = tokenService.findOrCreateToken(userContext, sessionContext);

            // then
            assertEquals("access-token", result.getAccessToken());
            assertEquals("refresh-token", result.getRefreshToken());
        }

        @Test
        @DisplayName("저장된 토큰 값 가져오기")
        void GET_SAVED_TOKEN() {

            // given
            UserContext userContext = new UserContext("social@test.com", "test-uuid");
            SessionContext sessionContext = new SessionContext("127.0.0.1", "Mozilla/5.0");

            AuthToken mockToken = AuthToken.builder()
                    .uuid(userContext.getUuid())
                    .email(userContext.getEmail())
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .ip(sessionContext.getIp())
                    .userAgent(sessionContext.getUserAgent())
                    .build();


            given(redisTokenRepository.findByUuid(userContext.getUuid())).willReturn(Optional.of(mockToken));

            // when
            TokenContext result = tokenService.getToken(userContext.getUuid());

            // then
            assertEquals("access-token", result.getAccessToken());
            assertEquals("refresh-token", result.getRefreshToken());
            assertEquals(userContext.getEmail(), result.getEmail());
            assertEquals(sessionContext.getIp(), result.getIp());
            assertEquals(sessionContext.getUserAgent(), result.getUserAgent());
        }

        @Test
        @DisplayName("토큰 재발급")
        void REISSUE_TOKEN() {

            // given
            UserContext userContext = new UserContext("social@test.com", "test-uuid");
            SessionContext sessionContext = new SessionContext("127.0.0.1", "Mozilla/5.0");

            AuthToken mockToken = AuthToken.builder()
                    .uuid(userContext.getUuid())
                    .email(userContext.getEmail())
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .ip(sessionContext.getIp())
                    .userAgent(sessionContext.getUserAgent())
                    .build();


            given(redisTokenRepository.findByUuid(userContext.getUuid())).willReturn(Optional.of(mockToken));

            // when
            TokenContext result = tokenService.getToken(userContext.getUuid());

            // then
            assertEquals("access-token", result.getAccessToken());
            assertEquals("refresh-token", result.getRefreshToken());
            assertEquals(userContext.getEmail(), result.getEmail());
            assertEquals(sessionContext.getIp(), result.getIp());
            assertEquals(sessionContext.getUserAgent(), result.getUserAgent());
        }
    }
}

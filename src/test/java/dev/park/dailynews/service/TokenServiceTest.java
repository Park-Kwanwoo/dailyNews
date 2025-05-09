package dev.park.dailynews.service;

import dev.park.dailynews.common.jwt.JwtToken;
import dev.park.dailynews.domain.user.AuthToken;
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
    private JwtToken jwtToken;
    @Mock
    private RedisTokenRepository redisTokenRepository;

    @Nested
    @DisplayName("성공 케이스")
    class SUCCESS_CASE {

        @Test
        @DisplayName("인증 토큰 반환 성공")
        void RETURN_TOKEN() {

            UserContext userContext = new UserContext("social@test.com", "테스터", "test-uuid");

            // given
            given(redisTokenRepository.findByEmail("social@test.com")).willReturn(Optional.empty());
            given(jwtToken.generateAccessToken(userContext)).willReturn("access-token");
            given(jwtToken.generateRefreshToken(userContext)).willReturn("refresh-token");
            given(redisTokenRepository.save(any(AuthToken.class))).willAnswer(i -> i.getArgument(0));

            // when
            AuthToken result = tokenService.findOrCreateToken(userContext);

            // then
            assertEquals("social@test.com", result.getEmail());
            assertEquals("test-uuid", result.getUuid());
            assertEquals("access-token", result.getAccessToken());
            assertEquals("refresh-token", result.getRefreshToken());

        }
    }
}

package dev.park.dailynews.jwt;

import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.InvalidTokenException;
import dev.park.dailynews.exception.TokenBlacklistException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.infra.auth.jwt.TokenValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TokenValidatorTest {

    @InjectMocks
    private TokenValidator tokenValidator;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("토큰이_유효한_경우_true_반환")
    void RETURN_TRUE_WHEN_TOKEN_AND_SESSION_VALID() {

        // given
        String fakeToken = "fake-token";

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        given(jwtUtils.isValidIssuer(fakeToken)).willReturn(true);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(fakeToken)).willReturn(null);

        // when
        boolean validToken = tokenValidator.validToken(fakeToken);

        // then
        assertTrue(validToken);
    }

    @Test
    @DisplayName("토큰이_유효하지_않은_경우_InvalidTokenException_예외_발생")
    void THROWS_JwtException_WHEN_TOKEN_INVALID() {

        // given
        String fakeToken = "fake-token";

        given(jwtUtils.isValidIssuer(fakeToken)).willThrow(JwtException.class);

        // expected
        assertThrows(InvalidTokenException.class, () -> tokenValidator.validToken(fakeToken));

    }

    @Test
    @DisplayName("토큰이_만료된_경우_만료_예외_발생")
    void THROWS_ExpiredTokenException_WHEN_TOKEN_EXPIRED() {

        // given
        String fakeToken = "fake-token";

        given(jwtUtils.isValidIssuer(fakeToken)).willThrow(ExpiredJwtException.class);

        // expected
        assertThrows(ExpiredTokenException.class, () -> tokenValidator.validToken(fakeToken));

    }

    @Test
    @DisplayName("블랙리스트_토큰으로_요청_시_예외_발생")
    void THROWS_TokenBlacklistException() {

        // given
        String fakeBlacklistToken = "fake-black-list-token";
        ValueOperations mockValueOperations = mock(ValueOperations.class);

        given(redisTemplate.opsForValue()).willReturn(mockValueOperations);
        given(mockValueOperations.get(fakeBlacklistToken)).willReturn(true);

        // expected
        assertThatThrownBy(() -> tokenValidator.validToken(fakeBlacklistToken))
                .isInstanceOf(TokenBlacklistException.class)
                .hasMessage("로그아웃된 토큰 정보입니다.");
    }
}

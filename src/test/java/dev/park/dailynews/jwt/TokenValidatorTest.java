package dev.park.dailynews.jwt;

import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.InvalidTokenException;
import dev.park.dailynews.infra.auth.jwt.JwtProperties;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.infra.auth.jwt.TokenValidator;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.model.TokenContext;
import dev.park.dailynews.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TokenValidatorTest {

    @InjectMocks
    private TokenValidator tokenValidator;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("토큰과_세션이_유효한_경우_true_반환")
    void RETURN_TRUE_WHEN_TOKEN_AND_SESSION_VALID() {

        // given
        String mockToken = "mock-token";
        String mockUUID = "mock-uuid";
        String mockEmail = "mock@email.com";
        String mockIssuer = "mock-issuer";
        SessionContext session = new SessionContext("127.0.0.1", "Mozilla/5.0");
        TokenContext savedToken = TokenContext.builder()
                .accessToken(mockToken)
                .email(mockEmail)
                .ip(session.getIp())
                .userAgent(session.getUserAgent())
                .build();

        given(jwtUtils.extractUUID(mockToken)).willReturn(mockUUID);
        given(jwtUtils.extractIssuer(mockToken)).willReturn(mockIssuer);
        given(jwtUtils.extractSubject(mockToken)).willReturn(mockEmail);
        given(jwtUtils.getJwtProperties()).willReturn(new JwtProperties("", 1, 1, mockIssuer));
        given(tokenService.getToken(mockUUID)).willReturn(savedToken);

        // when
        boolean validToken = tokenValidator.validToken(mockToken, session);

        // then
        assertTrue(validToken);
    }

    @Test
    @DisplayName("세션이_유효하지_않은_경우_false_반환")
    void RETURN_FALSE_WHEN_SESSION_INVALID() {

        // given
        String mockToken = "mock-token";
        String mockUUID = "mock-uuid";
        String mockEmail = "mock@email.com";
        String mockIssuer = "mock-issuer";
        SessionContext session = new SessionContext("127.0.0.1", "Mozilla/5.0");
        TokenContext savedToken = TokenContext.builder()
                .accessToken(mockToken)
                .email(mockEmail)
                .ip("127.0.0.2")
                .userAgent("Chrome")
                .build();

        given(jwtUtils.extractUUID(mockToken)).willReturn(mockUUID);
        given(jwtUtils.extractIssuer(mockToken)).willReturn(mockIssuer);
        given(jwtUtils.extractSubject(mockToken)).willReturn(mockEmail);
        given(tokenService.getToken(mockUUID)).willReturn(savedToken);

        // when
        boolean validToken = tokenValidator.validToken(mockToken, session);

        // then
        assertFalse(validToken);
    }

    @Test
    @DisplayName("토큰이_유효하지_않은_경우_InvalidTokenException_예외_발생")
    void THROWS_JwtException_WHEN_TOKEN_INVALID() {

        // given
        String mockToken = "mock-token";
        SessionContext session = new SessionContext("127.0.0.1", "Mozilla/5.0");

        given(jwtUtils.extractUUID(mockToken)).willThrow(JwtException.class);

        // expected
        assertThrows(InvalidTokenException.class, () -> tokenValidator.validToken(mockToken, session));

    }

    @Test
    @DisplayName("토큰이_만료된_경우_ExpiredTokenException_예외_발생")
    void THROWS_ExpiredTokenException_WHEN_TOKEN_EXPIRED() {

        // given
        String mockToken = "mock-token";
        SessionContext session = new SessionContext("127.0.0.1", "Mozilla/5.0");

        given(jwtUtils.extractUUID(mockToken)).willThrow(ExpiredJwtException.class);

        // expected
        assertThrows(ExpiredTokenException.class, () -> tokenValidator.validToken(mockToken, session));

    }
}

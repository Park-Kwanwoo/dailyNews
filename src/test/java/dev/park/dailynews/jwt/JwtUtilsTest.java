package dev.park.dailynews.jwt;


import dev.park.dailynews.infra.auth.jwt.JwtProperties;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    private JwtProperties jwtProperties = new JwtProperties(
            "This-is-very-long-dailyNews-test-secret-key",
            3000,
            86400,
            "test-issuer"
    );

    private JwtUtils jwtUtils = new JwtUtils(jwtProperties);


    @Test
    @DisplayName("accessToken_정상_발급_및_Claims_검증")
    void GENERATE_ACCESS_TOKEN() {

        // given
        UserContext userContext = new UserContext("test@jwt.com", "test-uuid", "test-socialToken", KAKAO);

        // when
        String accessToken = jwtUtils.generateAccessToken(userContext);
        Claims claims = jwtUtils.extractAllClaims(accessToken);

        // then
        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());
        assertEquals("test@jwt.com", claims.getSubject());
        assertEquals("test-uuid", claims.get("uuid", String.class));
        assertEquals("test-issuer", claims.getIssuer());

    }

    @Test
    @DisplayName("refreshToken_정상_발급_및_Claims_검증")
    void GENERATE_REFRESH_TOKEN() {

        // given
        UserContext userContext = new UserContext("test@jwt.com", "test-uuid", "test-socialToken", KAKAO);


        // when
        String refreshToken = jwtUtils.generateRefreshToken(userContext);
        Claims claims = jwtUtils.extractAllClaims(refreshToken);

        // then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertEquals("test-uuid", claims.get("uuid", String.class));
        assertEquals("test-issuer", claims.getIssuer());

    }

    @Test
    @DisplayName("만료된_토큰_ExpiredException_발생")
    void THROW_ExpiredException_WHEN_TOKEN_EXPIRED() throws InterruptedException {

        // given
        UserContext userContext = new UserContext("test@jwt.com", "test-uuid", "test-socialToken", KAKAO);


        // when
        String accessToken = jwtUtils.generateAccessToken(userContext);
        Thread.sleep(3000);

        // then
        assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractAllClaims(accessToken));
    }

    @Test
    @DisplayName("유효하지_않은_형식의_토큰_MalformedJwtException_발생")
    void THROW_MalformedJwtException_WHEN_TOKEN_INVALID() throws InterruptedException {

        // when
        String notJwtFormatToken = "not-a-valid-token";

        // then
        assertThrows(MalformedJwtException.class, () -> jwtUtils.extractAllClaims(notJwtFormatToken));
    }

    @Test
    @DisplayName("잘못된_서명의_토큰_SignatureException_발생")
    void THROW_SignatureException_WHEN_WRONG_SIGNATURE() {

        // given
        UserContext userContext = new UserContext("test@jwt.com", "test-uuid", "test-socialToken", KAKAO);


        // when
        JwtUtils wrongProvider = new JwtUtils(new JwtProperties(
                "This-is-very-long-dailyNews-wrong-secret-key",
                3000,
                8640,
                "test-issuer"));

        String wrongSignatureToken = wrongProvider.generateAccessToken(userContext);

        // then
        assertThrows(SignatureException.class, () -> jwtUtils.extractAllClaims(wrongSignatureToken));
    }
}

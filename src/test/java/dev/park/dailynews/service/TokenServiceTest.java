package dev.park.dailynews.service;

import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.token.TokenResponse;
import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.UnAuthorized;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.user.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("토큰_재발급_성공")
    void SUCCESS_REISSUE_TOKEN() {

        // given
        String fakeEmail = "fake@mail.com";
        String fakeSocialToken = "fake-social-token";
        String fakeRefreshToken = "fake-refresh-token";
        String fakeReissuedAccessToken = "fake-reissue-access-token";
        String fakeReissuedRefreshToken = "fake-reissue-refresh-token";
        User mockUser = mock(User.class);

        given(jwtUtils.extractSubject(fakeRefreshToken)).willReturn(fakeEmail);
        given(jwtUtils.extractSocialToken(fakeRefreshToken)).willReturn(fakeSocialToken);
        given(userRepository.findByEmail(fakeEmail)).willReturn(Optional.of(mockUser));
        given(jwtUtils.generateAccessToken(any(UserContext.class))).willReturn(fakeReissuedAccessToken);
        given(jwtUtils.generateRefreshToken(any(UserContext.class))).willReturn(fakeReissuedRefreshToken);

        // when
        TokenResponse tokenResponse = tokenService.reissueToken(fakeRefreshToken);

        // then
        assertEquals("fake-reissue-access-token", tokenResponse.getAccessToken());
        assertEquals("fake-reissue-refresh-token", tokenResponse.getRefreshToken());

        verify(jwtUtils, times(1)).extractSubject(anyString());
        verify(jwtUtils, times(1)).extractSocialToken(anyString());
        verify(userRepository, times(1)).findByEmail(fakeEmail);
        verify(jwtUtils, times(1)).generateAccessToken(any(UserContext.class));
        verify(jwtUtils, times(1)).generateRefreshToken(any(UserContext.class));
    }

    @Test
    @DisplayName("토큰_재발급_시_리프레시_토큰_만료_예외")
    void THROW_EXPIRED_TOKEN_EXCEPTION_WHEN_TOKEN_REISSUE() {

        // given
        String expiredRefreshToken = "expired-refresh-token";

        given(jwtUtils.extractSubject(expiredRefreshToken)).willThrow(ExpiredJwtException.class);

        // expected
        assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractSubject(expiredRefreshToken));

        // then
        verify(jwtUtils, times(1)).extractSubject(expiredRefreshToken);
        verify(jwtUtils, never()).extractSocialToken(expiredRefreshToken);
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtUtils, never()).generateAccessToken(any());
        verify(jwtUtils, never()).generateRefreshToken(any());

    }

    @Test
    @DisplayName("토큰_재발급_시_유효하지_않은_토큰_예외")
    void THROW_JWT_EXCEPTION_TOKEN_EXCEPTION_WHEN_TOKEN_REISSUE() {

        // given
        String invalidRefreshToken = "invalid-refresh-token";
        given(jwtUtils.extractSubject(invalidRefreshToken)).willThrow(JwtException.class);

        // when
        assertThrows(JwtException.class, () -> jwtUtils.extractSubject(invalidRefreshToken));

        // then
        verify(jwtUtils, times(1)).extractSubject(invalidRefreshToken);
        verify(jwtUtils, never()).extractSocialToken(invalidRefreshToken);
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtUtils, never()).generateAccessToken(any());
        verify(jwtUtils, never()).generateRefreshToken(any());

    }

}

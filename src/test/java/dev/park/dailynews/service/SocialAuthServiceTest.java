package dev.park.dailynews.service;

import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.NaverLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.dto.response.token.TokenResponse;
import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.InvalidTokenException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.SocialUserInfoContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class SocialAuthServiceTest {

    @InjectMocks
    private SocialAuthService socialAuthService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private SocialClientService socialClientService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("카카오_소셜_로그인_서비스_성공_시_토큰_응답")
    void RESPONSE_TOKEN_KAKAO_SOCIAL_LOGIN() {

        String mockAccessToken = "mock-access-token";
        String mockRefreshToken = "mock-refresh-token";
        SocialLoginParams mockSocialLoginParams = mock(KakaoLoginParams.class);
        SocialUserInfoContext mockUserInfo = SocialUserInfoContext.builder().email("test@mail.com").build();

        // given
        given(socialClientService.getUserInfo(any(SocialLoginParams.class))).willReturn(mockUserInfo);
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));
        given(jwtUtils.generateAccessToken(any(UserContext.class))).willReturn(mockAccessToken);
        given(jwtUtils.generateRefreshToken(any(UserContext.class))).willReturn(mockRefreshToken);

        // when
        TokenResponse tokenResponse = socialAuthService.login(mockSocialLoginParams);

        // then
        assertEquals("mock-access-token", tokenResponse.getAccessToken());
        assertEquals("mock-refresh-token", tokenResponse.getRefreshToken());
        verify(userRepository, times(1)).findByEmail(anyString());

    }

    @Test
    @DisplayName("네이버_소셜_로그인_서비스_성공_시_토큰_응답")
    void RESPONSE_TOKEN_NAVER_SOCIAL_LOGIN() {

        String mockAccessToken = "mock-access-token";
        String mockRefreshToken = "mock-refresh-token";
        SocialLoginParams mockSocialLoginParams = mock(NaverLoginParams.class);
        SocialUserInfoContext mockUserInfo = SocialUserInfoContext.builder().email("test@mail.com").build();

        // given
        given(socialClientService.getUserInfo(any(NaverLoginParams.class))).willReturn(mockUserInfo);
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));
        given(jwtUtils.generateAccessToken(any(UserContext.class))).willReturn(mockAccessToken);
        given(jwtUtils.generateRefreshToken(any(UserContext.class))).willReturn(mockRefreshToken);


        // when
        TokenResponse tokenResponse = socialAuthService.login(mockSocialLoginParams);

        // then
        assertEquals("mock-access-token", tokenResponse.getAccessToken());
        assertEquals("mock-refresh-token", tokenResponse.getRefreshToken());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("카카오_소셜_로그아웃")
    void KAKAO_SOCIAL_LOGOUT() {

        // given
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        String provider = KAKAO.toString();
        String socialToken = "kakao-token";
        String mockAccessToken = "mock-access-token";

        given(jwtUtils.extractExpiration(mockAccessToken)).willReturn(1000L);
        given(jwtUtils.extractProvider(mockAccessToken)).willReturn(provider);
        given(jwtUtils.extractSocialToken(mockAccessToken)).willReturn(socialToken);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        willDoNothing().given(valueOperations).set(eq(mockAccessToken), eq("Blacklist"), eq(1000L), eq(TimeUnit.SECONDS));
        willDoNothing().given(socialClientService).logout(any(SocialLogoutParams.class));

        // when
        socialAuthService.logout(mockAccessToken);

        // then
        verify(jwtUtils, times(1)).extractSocialToken(mockAccessToken);
        verify(jwtUtils, times(1)).extractProvider(mockAccessToken);
        verify(jwtUtils, times(1)).extractExpiration(mockAccessToken);
        verify(redisTemplate.opsForValue()).set(anyString(), anyString(), anyLong(), eq(TimeUnit.SECONDS));
        verify(socialClientService, times(1)).logout(any(SocialLogoutParams.class));
    }

    @Test
    @DisplayName("네이버_소셜_로그아웃")
    void NAVER_SOCIAL_LOGOUT() {

        // given
        String mockAccessToken = "mock-access-token";
        String provider = NAVER.toString();
        String socialToken = "naver-token";
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(jwtUtils.extractProvider(mockAccessToken)).willReturn(provider);
        given(jwtUtils.extractExpiration(mockAccessToken)).willReturn(10000L);
        given(jwtUtils.extractSocialToken(mockAccessToken)).willReturn(socialToken);
        willDoNothing().given(valueOperations).set(eq(mockAccessToken), eq("Blacklist"), eq(10000L), eq(TimeUnit.SECONDS));
        willDoNothing().given(socialClientService).logout(any(SocialLogoutParams.class));

        // when
        socialAuthService.logout(mockAccessToken);

        // then
        verify(redisTemplate.opsForValue(), times(1)).set(eq(mockAccessToken), eq("Blacklist"), eq(10000L), eq(TimeUnit.SECONDS));
        verify(socialClientService, times(1)).logout(any(SocialLogoutParams.class));
        verify(jwtUtils, times(1)).extractSocialToken(mockAccessToken);
        verify(jwtUtils, times(1)).extractProvider(mockAccessToken);
        verify(jwtUtils, times(1)).extractExpiration(mockAccessToken);
    }

    @Test
    @DisplayName("소셜_로그아웃_시_유효하지_않은_토큰_예외_발생")
    void THROW_INVALID_TOKEN_EXCEPTION_WHEN_TOKEN_INVALID() {

        // given
        String invalidToken = "invalid-token";

        given(jwtUtils.extractSocialToken(invalidToken)).willThrow(InvalidTokenException.class);

        // expected
        assertThrows(InvalidTokenException.class, () -> socialAuthService.logout(invalidToken));

        // then
        verify(redisTemplate, never()).opsForValue();
        verify(socialClientService, never()).logout(any(SocialLogoutParams.class));
    }

    @Test
    @DisplayName("소셜_로그아웃_시_만료된_토큰_예외_발생")
    void THROW_EXPIRED_TOKEN_EXCEPTION_WHEN_TOKEN_EXPIRED() {

        // given
        String invalidToken = "invalid-token";

        given(jwtUtils.extractSocialToken(invalidToken)).willThrow(ExpiredTokenException.class);

        // expected
        assertThrows(ExpiredTokenException.class, () -> socialAuthService.logout(invalidToken));

        // then
        verify(redisTemplate, never()).opsForValue();
        verify(socialClientService, never()).logout(any(SocialLogoutParams.class));
    }
}

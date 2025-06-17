package dev.park.dailynews.service;

import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.NaverLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.infra.social.KakaoClient;
import dev.park.dailynews.infra.social.NaverClient;
import dev.park.dailynews.infra.social.SocialClient;
import dev.park.dailynews.model.SocialUserInfoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialClientServiceTest {

    SocialClientService socialClientService;
    @Mock
    KakaoClient kakaoClient;

    @Mock
    NaverClient naverClient;

    @BeforeEach
    void setUp() {

        given(kakaoClient.socialProvider()).willReturn(KAKAO);
        given(naverClient.socialProvider()).willReturn(NAVER);

        List<SocialClient> clients = List.of(kakaoClient, naverClient);
        socialClientService = new SocialClientService(clients);
    }

    @Test
    @DisplayName("KAKAO_LOGIN_이면_KAKAO_CLIENT_반환")
    void RETURN_KAKAO_CLIENT_IF_KAKAO_LOGIN() {

        // given
        SocialLoginParams params = mock(KakaoLoginParams.class);
        String fakeKakaoToken = "kakao-token";

        SocialUserInfoContext kakaoContext = SocialUserInfoContext.builder()
                .id("kakao-id")
                .email("kakao@mail.com")
                .socialToken(fakeKakaoToken)
                .nickname("kakao")
                .provider(KAKAO)
                .build();

        given(params.getSocialProvider()).willReturn(KAKAO);
        given(kakaoClient.requestAccessToken(params)).willReturn(fakeKakaoToken);
        given(kakaoClient.requestUserInfo(fakeKakaoToken)).willReturn(kakaoContext);

        // when
        SocialUserInfoContext socialUserInfoContext = socialClientService.getUserInfo(params);

        // then
        verify(kakaoClient, times(1)).socialProvider();
        verify(kakaoClient, times(1)).requestAccessToken(params);
        verify(kakaoClient, times(1)).requestUserInfo(fakeKakaoToken);
        verify(naverClient, never()).requestAccessToken(any(SocialLoginParams.class));
        verify(naverClient, never()).requestUserInfo(anyString());

        assertEquals(KAKAO, socialUserInfoContext.getProvider());
        assertEquals("kakao-id", socialUserInfoContext.getId());
        assertEquals("kakao@mail.com", socialUserInfoContext.getEmail());
        assertEquals("kakao", socialUserInfoContext.getNickname());
        assertEquals(fakeKakaoToken, socialUserInfoContext.getSocialToken());

    }

    @Test
    @DisplayName("NAVER_LOGIN_이면_NAVER_CLIENT_반환")
    void RETURN_NAVER_CLIENT_IF_NAVER_LOGIN() {

        // given
        SocialLoginParams params = mock(NaverLoginParams.class);
        String fakeNaverToken = "fake-naver-token";

        SocialUserInfoContext naverContext = SocialUserInfoContext.builder()
                .id("naver-id")
                .email("naver@mail.com")
                .socialToken(fakeNaverToken)
                .nickname("naver")
                .provider(NAVER)
                .build();


        given(params.getSocialProvider()).willReturn(NAVER);
        given(naverClient.requestAccessToken(params)).willReturn(fakeNaverToken);
        given(naverClient.requestUserInfo(fakeNaverToken)).willReturn(naverContext);

        // when
        SocialUserInfoContext socialUserInfoContext = socialClientService.getUserInfo(params);

        // then
        assertEquals(NAVER, socialUserInfoContext.getProvider());
        assertEquals("fake-naver-token", socialUserInfoContext.getSocialToken());
        assertEquals("naver-id", socialUserInfoContext.getId());
        assertEquals("naver@mail.com", socialUserInfoContext.getEmail());
        assertEquals("naver", socialUserInfoContext.getNickname());


        verify(naverClient, times(1)).requestAccessToken(params);
        verify(naverClient, times(1)).requestUserInfo(fakeNaverToken);
        verify(kakaoClient, never()).requestAccessToken(any(SocialLoginParams.class));
        verify(kakaoClient, never()).requestUserInfo(anyString());
    }

    @Test
    @DisplayName("KAKAO_LOGOUT_이면_KAKAO_CLIENT_반환")
    void RETURN_KAKAO_CLIENT_IF_KAKAO_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);

        given(params.getProvider()).willReturn(KAKAO);
        willDoNothing().given(kakaoClient).logout(params);

        // when
        socialClientService.logout(params);

        // then
        verify(params, times(1)).getProvider();
        verify(kakaoClient, times(1)).logout(params);
        verify(naverClient, never()).logout(params);

    }

    @Test
    @DisplayName("NAVER_LOGOUT_이면_NAVER_CLIENT_반환")
    void RETURN_NAVER_CLIENT_IF_NAVER_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);

        given(params.getProvider()).willReturn(NAVER);
        willDoNothing().given(naverClient).logout(params);

        // when
        socialClientService.logout(params);

        // then
        verify(params, times(1)).getProvider();
        verify(naverClient, times(1)).logout(params);
        verify(kakaoClient, never()).logout(params);
    }
}
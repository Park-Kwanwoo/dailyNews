package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.KakaoToken;
import dev.park.dailynews.domain.social.KakaoUserInfo;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.exception.ExternalApiException;
import dev.park.dailynews.exception.ExternalApiTimeoutException;
import dev.park.dailynews.model.SocialUserInfoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class KakaoClientUnitTest {
    private KakaoClient kakaoClient;
    private RestTemplate rt;


    static KakaoProperties kakaoProperties() {
        return new KakaoProperties("authorization_code",
                "kakao-client-id",
                "kakao-client-secret",
                "redirectUrl",
                "tokenUrl",
                "userInfoUrl",
                "logoutUrl");

    }
    @BeforeEach
    void setUp() {
        rt = mock(RestTemplate.class);
        kakaoClient = new KakaoClient(kakaoProperties(), rt);
    }

    @Test
    @DisplayName("카카오_토큰_요청_시_토큰_응답")
    void RESPONSE_TOKEN_WHEN_REQUEST_TO_KAKAO() {

        SocialLoginParams params = mock(KakaoLoginParams.class);
        given(params.getCode()).willReturn("test-code");
        KakaoToken fakeToken = new KakaoToken("access-token", "refresh-token", "Bearer", 100L, "account_email profile", 1000L);
        given(rt.postForObject(eq("tokenUrl"), any(), eq(KakaoToken.class)))
                .willReturn(fakeToken);

        // when
        String accessToken = kakaoClient.requestAccessToken(params);

        // then
        assertEquals("access-token", accessToken);
        verify(rt, times(1)).postForObject(eq("tokenUrl"), any(), eq(KakaoToken.class));
    }

    @Test
    @DisplayName("카카오_유저_정보_요청_시_사용자_정보_응답")
    void RESPONSE_USER_INFO_WHEN_REQUEST_TO_KAKAO() {

        // given
        String fakeToken = "fake-token";

        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo("kakao-id",
                new KakaoUserInfo.KakaoAccount("kakao@mail.com",
                        new KakaoUserInfo.KakaoAccount.Profile("kakao")));
        given(rt.postForObject(eq("userInfoUrl"), any(), eq(KakaoUserInfo.class)))
                .willReturn(kakaoUserInfo);

        // when
        SocialUserInfoContext context = kakaoClient.requestUserInfo(fakeToken);

        // then
        assertEquals("kakao@mail.com", context.getEmail());
        assertEquals("kakao", context.getNickname());
        assertEquals(fakeToken, context.getSocialToken());
        assertEquals(KAKAO, context.getProvider());
    }

    @Test
    @DisplayName("카카오_로그아웃_요청_성공")
    void SUCCESS_KAKAO_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);
        given(params.getProvider()).willReturn(KAKAO);

        String kakaoId = "kakao-id";

        given(rt.postForObject(eq("logoutUrl"), any(), eq(String.class)))
                .willReturn(kakaoId);

        // when
        kakaoClient.logout(params);

        // then
        verify(rt, times(1)).postForObject(eq("logoutUrl"), any(), eq(String.class));
    }

    @Test
    @DisplayName("카카오_토큰_요청_시_연결_에러")
    void THROWS_CONNECTION_EXCEPTION_WHEN_REQUEST_TOKEN() {
        
        // given
        SocialLoginParams params = mock(SocialLoginParams.class);
        given(params.getSocialProvider()).willReturn(KAKAO);

        given(rt.postForObject(eq("tokenUrl"), any(), any()))
                .willThrow(ResourceAccessException.class);

        // expected
        assertThatThrownBy(() -> kakaoClient.requestAccessToken(params))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }

    @Test
    @DisplayName("카카오_사용자_정보_요청_시_연결_에러")
    void THROWS_CONNECTION_EXCEPTION_WHEN_REQUEST_USER_INFO() {

        // given
        String fakeToken = "fake-token";

        given(rt.postForObject(eq("userInfoUrl"), any(), any()))
                .willThrow(ResourceAccessException.class);

        // expected
        assertThatThrownBy(() -> kakaoClient.requestUserInfo(fakeToken))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }

    @Test
    @DisplayName("카카오_로그아웃_요청_시_연결_에러")
    void THROWS_CONNECTION_EXCEPTION_WHEN_REQUEST_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);
        given(params.getProvider()).willReturn(KAKAO);

        given(rt.postForObject(eq("logoutUrl"), any(), any()))
                .willThrow(ResourceAccessException.class);

        // expected
        assertThatThrownBy(() -> kakaoClient.logout(params))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }

    @Test
    @DisplayName("카카오_토큰_요청_시_요청_에러")
    void THROWS_REQUEST_EXCEPTION_WHEN_REQUEST_TOKEN() {

        // given
        SocialLoginParams params = mock(SocialLoginParams.class);
        given(params.getSocialProvider()).willReturn(KAKAO);

        given(rt.postForObject(eq("tokenUrl"), any(), any()))
                .willThrow(RestClientException.class);

        // expected
        assertThatThrownBy(() -> kakaoClient.requestAccessToken(params))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }

    @Test
    @DisplayName("카카오_사용자_정보_요청_시_연결_에러")
    void THROWS_REQUEST_EXCEPTION_WHEN_REQUEST_USER_INFO() {

        // given
        String fakeToken = "fake-token";

        given(rt.postForObject(eq("userInfoUrl"), any(), any()))
                .willThrow(RestClientException.class);

        // expected
        assertThatThrownBy(() -> kakaoClient.requestUserInfo(fakeToken))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }

    @Test
    @DisplayName("카카오_로그아웃_요청_시_연결_에러")
    void THROWS_REQUEST_EXCEPTION_WHEN_REQUEST_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);
        given(params.getProvider()).willReturn(KAKAO);

        given(rt.postForObject(eq("logoutUrl"), any(), any()))
                .willThrow(RestClientException.class);

        // expected
        assertThatThrownBy(() -> kakaoClient.logout(params))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }
}
package dev.park.dailynews.infra.social;

import dev.park.dailynews.domain.social.NaverToken;
import dev.park.dailynews.domain.social.NaverUserInfo;
import dev.park.dailynews.dto.response.sosical.NaverLoginParams;
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

import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class NaverClientUnitTest {

    private NaverClient naverClient;

    private RestTemplate rt;

    static NaverProperties naverProperties() {
        return new NaverProperties("authorization_code",
                "delete",
                "naver-client-id",
                "naver-secret",
                "tokenUrl",
                "userInfoUrl"
        );
    }

    @BeforeEach
    void setUp() {
        rt = mock(RestTemplate.class);
        naverClient = new NaverClient(naverProperties(), rt);
    }

    @Test
    @DisplayName("네이버_토큰_요청_시_토큰_응답")
    void RESPONSE_TOKEN_WHEN_REQUEST_TO_NAVER() {

        SocialLoginParams params = mock(NaverLoginParams.class);
        given(params.getCode()).willReturn("test-code");
        NaverToken fakeToken = new NaverToken("access-token", "refresh-token", "Bearer", 1230249L);
        given(rt.postForObject(eq("tokenUrl"), any(), eq(NaverToken.class)))
                .willReturn(fakeToken);

        // when
        String accessToken = naverClient.requestAccessToken(params);

        // then
        assertEquals("access-token", accessToken);
        verify(rt, times(1)).postForObject(eq("tokenUrl"), any(), eq(NaverToken.class));
    }

    @Test
    @DisplayName("네이버_유저_정보_요청_시_사용자_정보_응답")
    void RESPONSE_USER_INFO_WHEN_REQUEST_TO_NAVER() {

        // given
        String fakeToken = "fake-token";

        NaverUserInfo naverUserInfo = new NaverUserInfo(
                new NaverUserInfo.Response("naver@mail.com", "naver", "naver-id"));

        given(rt.postForObject(eq("userInfoUrl"), any(), eq(NaverUserInfo.class)))
                .willReturn(naverUserInfo);

        // when
        SocialUserInfoContext context = naverClient.requestUserInfo(fakeToken);

        // then
        assertEquals("naver@mail.com", context.getEmail());
        assertEquals("naver", context.getNickname());
        assertEquals(fakeToken, context.getSocialToken());
        assertEquals(NAVER, context.getProvider());
    }

    @Test
    @DisplayName("네이버_로그아웃_요청_성공")
    void SUCCESS_NAVER_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);
        given(params.getProvider()).willReturn(NAVER);

        String logoutResponse = """
                {
                  "accessToken": "naver-token",
                  "result": "success"
                }
                """;

        given(rt.postForObject(eq("tokenUrl"), any(), eq(Object.class)))
                .willReturn(logoutResponse);

        // when
        naverClient.logout(params);

        // then
        verify(rt, times(1)).postForObject(eq("tokenUrl"), any(), eq(Object.class));
    }

    @Test
    @DisplayName("네이버_토큰_요청_시_연결_에러")
    void THROWS_CONNECTION_EXCEPTION_WHEN_REQUEST_TOKEN() {

        // given
        SocialLoginParams params = mock(SocialLoginParams.class);
        given(params.getSocialProvider()).willReturn(NAVER);

        given(rt.postForObject(eq("tokenUrl"), any(), any()))
                .willThrow(ResourceAccessException.class);

        // expected
        assertThatThrownBy(() -> naverClient.requestAccessToken(params))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }

    @Test
    @DisplayName("네이버_사용자_정보_요청_시_연결_에러")
    void THROWS_CONNECTION_EXCEPTION_WHEN_REQUEST_USER_INFO() {

        // given
        String fakeToken = "fake-token";

        given(rt.postForObject(eq("userInfoUrl"), any(), any()))
                .willThrow(ResourceAccessException.class);

        // expected
        assertThatThrownBy(() -> naverClient.requestUserInfo(fakeToken))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }

    @Test
    @DisplayName("네이버_로그아웃_요청_시_연결_에러")
    void THROWS_CONNECTION_EXCEPTION_WHEN_REQUEST_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);
        given(params.getProvider()).willReturn(NAVER);

        given(rt.postForObject(eq("tokenUrl"), any(), any()))
                .willThrow(ResourceAccessException.class);

        // expected
        assertThatThrownBy(() -> naverClient.logout(params))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }

    @Test
    @DisplayName("네이버_토큰_요청_시_요청_에러")
    void THROWS_REQUEST_EXCEPTION_WHEN_REQUEST_TOKEN() {

        // given
        SocialLoginParams params = mock(SocialLoginParams.class);
        given(params.getSocialProvider()).willReturn(NAVER);

        given(rt.postForObject(eq("tokenUrl"), any(), any()))
                .willThrow(RestClientException.class);

        // expected
        assertThatThrownBy(() -> naverClient.requestAccessToken(params))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }

    @Test
    @DisplayName("네이버_사용자_정보_요청_시_연결_에러")
    void THROWS_REQUEST_EXCEPTION_WHEN_REQUEST_USER_INFO() {

        // given
        String fakeToken = "fake-token";

        given(rt.postForObject(eq("userInfoUrl"), any(), any()))
                .willThrow(RestClientException.class);

        // expected
        assertThatThrownBy(() -> naverClient.requestUserInfo(fakeToken))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }

    @Test
    @DisplayName("네이버_로그아웃_요청_시_연결_에러")
    void THROWS_REQUEST_EXCEPTION_WHEN_REQUEST_LOGOUT() {

        // given
        SocialLogoutParams params = mock(SocialLogoutParams.class);
        given(params.getProvider()).willReturn(NAVER);

        given(rt.postForObject(eq("tokenUrl"), any(), any()))
                .willThrow(RestClientException.class);

        // expected
        assertThatThrownBy(() -> naverClient.logout(params))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }
}
package dev.park.dailynews.infra.social;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.exception.ExternalApiException;
import dev.park.dailynews.exception.ExternalApiTimeoutException;
import dev.park.dailynews.model.SocialUserInfoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@WireMockTest(httpPort = 8090)
class KakaoClientTest {
    private KakaoClient kakaoClient;
    static KakaoProperties kakaoProperties() {
        return new KakaoProperties("authorization_code",
                "kakao-client-id",
                "kakao-secret",
                "http://localhost:8090",
                "http://localhost:8090/oauth/token",
                "http://localhost:8090/v2/user/me",
                "http://localhost:8090/v1/user/logout");

    }

    @BeforeEach
    void setUp() {
        RestTemplate rt = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        kakaoClient = new KakaoClient(kakaoProperties(), rt);
    }

    @Test
    @DisplayName("카카오_토큰_요청_성공")
    void REQUEST_KAKAO_TOKEN_SUCCESS() throws IOException {

        // given
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/kakaoTokenResponse.json");
        String kakaoTokenRespose = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        WireMock.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(kakaoTokenRespose)
                )
        );

        SocialLoginParams mockParams = mock(KakaoLoginParams.class);
        given(mockParams.getCode()).willReturn("kakao-code");

        // when
        String accessToken = kakaoClient.requestAccessToken(mockParams);

        // then
        assertEquals("test-access-token", accessToken);
    }

    @Test
    @DisplayName("카카오_사용자_정보_요청_성공")
    void REQUEST_KAKAO_USER_INFO_SUCCESS() throws IOException {

        // given
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/kakaoUserInfoResponse.json");
        String kakaoTokenRespose = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String fakeKakaoToken = "fake-kakao-token";

        stubFor(post(urlEqualTo("/v2/user/me"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(kakaoTokenRespose)
                        )
        );

        // when
        SocialUserInfoContext kakaoUserContext = kakaoClient.requestUserInfo(fakeKakaoToken);

        // then
        assertEquals("kakao", kakaoUserContext.getNickname());
        assertEquals("kakao@mail.com", kakaoUserContext.getEmail());
        assertEquals(KAKAO, kakaoUserContext.getProvider());
        assertEquals(fakeKakaoToken, kakaoUserContext.getSocialToken());
    }

    @Test
    @DisplayName("카카오_로그아웃_요청_성공")
    void REQUEST_KAKAO_LOGOUT_SUCCESS() throws IOException {

        // given
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/kakaoLogoutResponse.json");
        String kakaoLogoutRespose = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String fakeKakaoToken = "fake-kakao-token";


        stubFor(post(urlEqualTo("/v1/user/logout"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(kakaoLogoutRespose)
                )
        );

        SocialLogoutParams mockParams = mock(SocialLogoutParams.class);
        given(mockParams.getToken()).willReturn(fakeKakaoToken);

        // when
        kakaoClient.logout(mockParams);
    }

    @Test
    @DisplayName("카카오_토큰_요청_시_타임아웃_예외_발생")
    void CONNECTION_EXCEPTION_WHEN_REQUEST_KAKAO_TOKEN() {

        // given
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withFixedDelay(6000)
                ));

        SocialLoginParams mockParams = mock(KakaoLoginParams.class);
        given(mockParams.getCode()).willReturn("kakao-code");

        // expected
        assertThrows(ExternalApiTimeoutException.class,
                () -> kakaoClient.requestAccessToken(mockParams));
    }

    @Test
    @DisplayName("카카오_토큰_요청_카카오_서버_에러_예외")
    void THROWS_EXCEPTION_WHEN_KAKAO_SERVER_ERROR() {

        // given
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody("{\"error\":\"server_error\"}")
                ));

        SocialLoginParams mockParams = mock(KakaoLoginParams.class);
        given(mockParams.getCode()).willReturn("fake-code");

        // then
        assertThrows(ExternalApiException.class, () -> kakaoClient.requestAccessToken(mockParams));
    }

    @Test
    @DisplayName("카카오_토큰_유효하지_않은_파라미터_요청_시_예외")
    void THROWS_EXCEPTION_WHEN_TOKEN_REQUEST_PARAMETER_INVALID() {

        // given
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody("{\"error\":\"invalid_request\"}")
                ));

        SocialLoginParams mockParams = mock(KakaoLoginParams.class);
        given(mockParams.getCode()).willReturn("invalid-code");

        // then
        assertThrows(ExternalApiException.class, () -> kakaoClient.requestAccessToken(mockParams));
    }

    @Test
    @DisplayName("카카오_토큰_요청_사용자_인증_실패_예외")
    void THROWS_EXCEPTION_WHEN_REQUEST_UNAUTHORIZED() {

        // given
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody("{\"error\":\"unauthorized\"}")
                ));

        SocialLoginParams mockParams = mock(KakaoLoginParams.class);
        given(mockParams.getCode()).willReturn("invalid-code");

        // then
        assertThrows(ExternalApiException.class, () -> kakaoClient.requestAccessToken(mockParams));
    }

}
package dev.park.dailynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.park.dailynews.config.DailyTest;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.NaverLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DailyTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8090)
@ActiveProfiles("test")
class SocialAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("카카오_소셜_로그인_성공_및_회원가입")
    void kakaoLoginAndSignupSuccess() throws Exception {

        // given
        InputStream tokenStream = getClass().getClassLoader().getResourceAsStream("json/kakaoTokenResponse.json");
        String kakaoTokenRespose = IOUtils.toString(tokenStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(kakaoTokenRespose)
                )
        );

        InputStream userInfoStream = getClass().getClassLoader().getResourceAsStream("json/kakaoUserInfoResponse.json");
        String kakaoUserInfoResponse = IOUtils.toString(userInfoStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/v2/user/me"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(kakaoUserInfoResponse)
                )
        );

        SocialLoginParams params = new KakaoLoginParams("kakao-code");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andDo(print());

        User savedUser = userRepository.findByEmail("kakao@mail.com")
                .orElseThrow(UserNotFoundException::new);
        // then (DB에 회원 생성 확인)
        assertEquals("kakao@mail.com", savedUser.getEmail());
        assertEquals(KAKAO, savedUser.getProvider());
        assertEquals("kakao", savedUser.getNickname());
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("네이버_소셜_로그인_성공_및_회원가입")
    void naverLoginAndSignupSuccess() throws Exception {

        // given
        InputStream tokenStream = getClass().getClassLoader().getResourceAsStream("json/naverTokenResponse.json");
        String naverTokenRespose = IOUtils.toString(tokenStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/oauth2.0/token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(naverTokenRespose)
                )
        );

        InputStream userInfoStream = getClass().getClassLoader().getResourceAsStream("json/naverUserInfoResponse.json");
        String naverUserInfoResponse = IOUtils.toString(userInfoStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/v1/nid/me"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(naverUserInfoResponse)
                )
        );

        SocialLoginParams params = new NaverLoginParams("naver-code", "test");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andDo(print());

        User savedUser = userRepository.findByEmail("naver@mail.com")
                .orElseThrow(UserNotFoundException::new);

        // then (DB에 회원 생성 확인)
        assertEquals("naver@mail.com", savedUser.getEmail());
        assertEquals(NAVER, savedUser.getProvider());
        assertEquals("naver", savedUser.getNickname());
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원_정보_응답")
    void RESPONSE_USER_INFO() throws Exception {

        // given
        User user = User.builder()
                .email("kakao@mail.com")
                .nickname("카카오")
                .provider(KAKAO)
                .build();

        User savedUser = userRepository.save(user);
        UserContext userContext = UserContext.from(savedUser, "kakao-token");
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(get("/social/info")
                        .header(AUTHORIZATION, accessToken))
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.nickname").value("카카오"))
                .andExpect(jsonPath("$.data.email").value("kakao@mail.com"))
                .andExpect(jsonPath("$.data.provider").value("KAKAO"))
                .andDo(print());
    }


    @Test
    @DisplayName("카카오_소셜_로그인_토큰_요청_타임_아웃_발생")
    void KAKAO_LOGIN_TOKEN_REQUEST_TIMEOUT() throws Exception {

        // given
        stubFor(WireMock.post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withFixedDelay(6000)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new KakaoLoginParams("kakao-code");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("연결에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("카카오_소셜_로그인_사용자_정보_요청_타임_아웃_발생")
    void KAKAO_LOGIN_USER_INFO_REQUEST_TIMEOUT() throws Exception {

        // given
        InputStream tokenStream = getClass().getClassLoader().getResourceAsStream("json/kakaoTokenResponse.json");
        String kakaoTokenRespose = IOUtils.toString(tokenStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(kakaoTokenRespose)
                )
        );

        stubFor(WireMock.post(urlEqualTo("/v2/user/me"))
                .willReturn(
                        aResponse()
                                .withFixedDelay(6000)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new KakaoLoginParams("kakao-code");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("연결에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("네이버_소셜_로그인_토큰_요청_타임_아웃_발생")
    void NAVER_LOGIN_TOKEN_REQUEST_TIMEOUT() throws Exception {

        // given
        stubFor(WireMock.post(urlEqualTo("/oauth2.0/token"))
                .willReturn(
                        aResponse()
                                .withFixedDelay(6000)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new NaverLoginParams("naver-code", "test");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("연결에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("네이버_소셜_로그인_사용자_정보_요청_타임_아웃_발생")
    void NAVER_LOGIN_USER_INFO_REQUEST_TIMEOUT() throws Exception {

        // given
        InputStream tokenStream = getClass().getClassLoader().getResourceAsStream("json/naverTokenResponse.json");
        String naverTokenRespose = IOUtils.toString(tokenStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/oauth2.0/token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(naverTokenRespose)
                )
        );

        stubFor(WireMock.post(urlEqualTo("/v1/nid/me"))
                .willReturn(
                        aResponse()
                                .withFixedDelay(6000)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new NaverLoginParams("naver-code", "test");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("연결에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("카카오_소셜_로그인_토큰_요청_에러_발생")
    void KAKAO_LOGIN_TOKEN_REQUEST_EXCEPTION() throws Exception {

        // given
        stubFor(WireMock.post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withStatus(401)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new KakaoLoginParams("kakao-code");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("요청에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("카카오_소셜_로그인_사용자_정보_요청_에러_발생")
    void KAKAO_LOGIN_USER_INFO_REQUEST_EXCEPTION() throws Exception {

        // given
        InputStream tokenStream = getClass().getClassLoader().getResourceAsStream("json/kakaoTokenResponse.json");
        String kakaoTokenRespose = IOUtils.toString(tokenStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(kakaoTokenRespose)
                )
        );

        stubFor(WireMock.post(urlEqualTo("/v2/user/me"))
                .willReturn(
                        aResponse()
                                .withStatus(500)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new KakaoLoginParams("kakao-code");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("요청에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("네이버_소셜_로그인_토큰_요청_타임_아웃_발생")
    void NAVER_LOGIN_TOKEN_REQUEST_EXCEPTION() throws Exception {

        // given
        stubFor(WireMock.post(urlEqualTo("/oauth2.0/token"))
                .willReturn(
                        aResponse()
                                .withStatus(401)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new NaverLoginParams("naver-code", "test");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("요청에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("네이버_소셜_로그인_사용자_정보_요청_타임_아웃_발생")
    void NAVER_LOGIN_USER_INFO_REQUEST_EXCEPTION() throws Exception {

        // given
        InputStream tokenStream = getClass().getClassLoader().getResourceAsStream("json/naverTokenResponse.json");
        String naverTokenRespose = IOUtils.toString(tokenStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/oauth/token"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(naverTokenRespose)
                )
        );

        stubFor(WireMock.post(urlEqualTo("/v1/nid/me"))
                .willReturn(
                        aResponse()
                                .withStatus(500)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                )
        );

        SocialLoginParams params = new NaverLoginParams("naver-code", "test");
        String loginRequest = objectMapper.writeValueAsString(params);

        // expected
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("요청에 실패했습니다."))
                .andDo(print());
    }
}
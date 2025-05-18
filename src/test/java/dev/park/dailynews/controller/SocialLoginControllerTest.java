package dev.park.dailynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.NaverLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.repository.RedisTokenRepository;
import dev.park.dailynews.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WireMockTest(httpPort = 80)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SocialLoginControllerTest {

    private final static String MOCK_KAKAO_TOKEN_URL = "/oauth/token";
    private final static String MOCK_KAKAO_USER_INFO_URI = "/v2/user/me";

    private final static String MOCK_NAVER_TOKEN_URL = "/oauth2.0/token";
    private final static String MOCK_NAVER_USER_INFO_URI = "/v1/nid/me";

    private final static String APPLICATION_FORM_URLENCODED_UTF8_VALUE = "application/x-www-form-urlencoded;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTokenRepository tokenRepository;

    private InputStream inputStream = InputStream.nullInputStream();


    @BeforeEach
    void clean() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("카카오_소셜_로그인_성공")
    void SUCCESS_KAKAO_SOCIAL_LOGIN() throws Exception {

        // given
        inputStream = getClass().getClassLoader().getResourceAsStream("json/kakaoTokenResponse.json");
        String tokenResponse = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo(MOCK_KAKAO_TOKEN_URL))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED_UTF8_VALUE))
                .willReturn(
                        aResponse()
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(tokenResponse)
                )
        );

        inputStream = getClass().getClassLoader().getResourceAsStream("json/kakaoUserInfoResponse.json");
        String userInfoResponse = IOUtils.toString(inputStream, StandardCharsets.UTF_8);


        stubFor(WireMock.post(urlEqualTo(MOCK_KAKAO_USER_INFO_URI))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED_UTF8_VALUE))
                .withHeader(AUTHORIZATION, equalTo("Bearer test-access-token"))
                .willReturn(
                        aResponse()
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(userInfoResponse)
                )
        );

        SocialLoginParams params = new KakaoLoginParams("test-code");
        String json = objectMapper.writeValueAsString(params);
        // when
        mockMvc.perform(post("/social/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        User user = userRepository.findByEmail("test@kakao.com")
                .orElseThrow(UserNotFoundException::new);

        assertEquals("test@kakao.com", user.getEmail());
        assertEquals("kakao", user.getNickname());
        assertEquals(KAKAO, user.getProvider());
    }

    @Test
    @DisplayName("네이버_소셜_로그인_성공")
    void SUCCESS_NAVER_SOCIAL_LOGIN() throws Exception {

        // given
        inputStream = getClass().getClassLoader().getResourceAsStream("json/naverTokenResponse.json");
        String tokenResponse = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        stubFor(WireMock.post(urlEqualTo(MOCK_NAVER_TOKEN_URL))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED_UTF8_VALUE))
                .willReturn(
                        aResponse()
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(tokenResponse)
                )
        );

        inputStream = getClass().getClassLoader().getResourceAsStream("json/naverUserInfoResponse.json");
        String userInfoResponse = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        stubFor(WireMock.post(urlEqualTo(MOCK_NAVER_USER_INFO_URI))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED_UTF8_VALUE))
                .willReturn(
                        aResponse()
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(userInfoResponse)
                )
        );

        SocialLoginParams params = new NaverLoginParams("test-naver-code", "test-state");
        String json = objectMapper.writeValueAsString(params);
        // when
        mockMvc.perform(post("/social/login")
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        User result = userRepository.findByEmail("test@naver.com")
                .orElseThrow(UserNotFoundException::new);

        assertEquals("test@naver.com", result.getEmail());
        assertEquals("naver", result.getNickname());
        assertEquals(NAVER, result.getProvider());
    }
}
package dev.park.dailynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.MultiValuePattern;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.repository.RedisSessionRepository;
import dev.park.dailynews.repository.RedisTokenRepository;
import dev.park.dailynews.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
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

    private final static String APPLICATION_FORM_URLENCODED_UTF8_VALUE = "application/x-www-form-urlencoded;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTokenRepository tokenRepository;

    @Autowired
    private RedisSessionRepository sessionRepository;

    private InputStream inputStream = InputStream.nullInputStream();

    @BeforeEach
    void clean() {
        tokenRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    public Map<String, MultiValuePattern> setTokenBody(String code) {

        Map<String, MultiValuePattern> tokenBody = new HashMap<>();

        tokenBody.put("grant_type", havingExactly("test-authorization-code"));
        tokenBody.put("client_id", havingExactly("test-client-id"));
        tokenBody.put("redirect_uri", havingExactly("http://localhost:80/social/test"));
        tokenBody.put("client_secret", havingExactly("test-client-secret"));
        tokenBody.put("code", havingExactly(code));

        return tokenBody;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SUCCESS_CASE {

        @Test
        @DisplayName("소셜 로그인 성공")
        void SUCCESS_SOCIAL_LOGIN() throws Exception {

            // given
            SocialLoginParams params = new KakaoLoginParams("test-code");
            String requestBody = objectMapper.writeValueAsString(params);
            Map<String, MultiValuePattern> tokenBody = setTokenBody(params.getCode());

            inputStream = getClass().getClassLoader().getResourceAsStream("json/kakaoTokenResponse.json");
            String tokenResponse = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            stubFor(WireMock.post(urlEqualTo(MOCK_KAKAO_TOKEN_URL))
                    .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED_UTF8_VALUE))
                    .withFormParams(tokenBody)
                    .willReturn(aResponse()
                            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                            .withBody(tokenResponse)));

            inputStream = getClass().getClassLoader().getResourceAsStream("json/kakaoUserInfoResponse.json");
            String userInfoResponse = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            Map<String, MultiValuePattern> userInfoBody = new HashMap<>();
            userInfoBody.put("property_keys", havingExactly("[\"kakao_account.email\", \"kakao_account.profile.nickname\"]"));

            stubFor(WireMock.post(urlEqualTo(MOCK_KAKAO_USER_INFO_URI))
                    .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED_UTF8_VALUE))
                    .withHeader(AUTHORIZATION, equalTo("Bearer test-access-token"))
                    .withFormParams(userInfoBody)
                    .willReturn(
                            aResponse()
                                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                    .withBody(userInfoResponse)
                    )
            );

            // when
            mockMvc.perform(post("/login/kakao")
                            .contentType(APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            User user = userRepository.findByEmail("dailyNews@kakao.com")
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

            assertEquals("dailyNews@kakao.com", user.getEmail());
            assertEquals("테스터", user.getNickname());
            assertEquals(SocialProvider.KAKAO, user.getProvider());
        }
    }
}
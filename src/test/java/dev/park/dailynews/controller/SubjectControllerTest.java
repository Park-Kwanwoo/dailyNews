package dev.park.dailynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.park.dailynews.config.DailyTest;
import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.request.SubjectUpdateRequest;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.subject.SubjectRepository;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DailyTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 8090)
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    private static final String UUID = "test-uuid";

    private static final String USER_EMAIL = "test@mail.com";
    private static final String USER_NICKNAME = "테스터";
    private static final String SOCIAL_TOKEN = "test-social-token";

    @BeforeEach
    void clean() {
        saveUser();
    }

    void saveUser() {
        User user = User.builder()
                .email(USER_EMAIL)
                .nickname(USER_NICKNAME)
                .provider(NAVER)
                .build();

        userRepository.save(user);
    }

    void saveSubject() {

        User savedUser = userRepository.findByEmail(USER_EMAIL)
                .orElseThrow(UserNotFoundException::new);

        Subject subject = Subject.builder()
                .keyword("AI 전망")
                .build();
        savedUser.setSubject(subject);

        subjectRepository.save(subject);
    }

    UserContext createUserContext() {
        return UserContext.builder()
                .uuid(UUID)
                .email(USER_EMAIL)
                .socialToken(SOCIAL_TOKEN)
                .provider(NAVER)
                .build();
    }

    @Test
    @DisplayName("Subject_등록_성공")
    void SUCCESS_REGISTER_SUBJECT() throws Exception {

        // given
        InputStream keywordStream = getClass().getClassLoader().getResourceAsStream("json/validKeywordResponse.json");
        String keywordValidateResponse = IOUtils.toString(keywordStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(keywordValidateResponse)
                ));

        SubjectRequest subjectRequest = new SubjectRequest("AI 전망");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(post("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andDo(print());

    }
    
    @Test
    @DisplayName("Subject_수정_성공")
    @Transactional
    void SUCCESS_UPDATE_SUBJECT() throws Exception {
        
        // given
        saveSubject();
        InputStream keywordStream = getClass().getClassLoader().getResourceAsStream("json/validKeywordResponse.json");
        String validKeywordResponse = IOUtils.toString(keywordStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(validKeywordResponse)
                ));


        SubjectRequest subjectRequest = new SubjectRequest("비트코인 전망");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(patch("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andDo(print());
    }

    @Test
    @DisplayName("부적절한_주제_등록_시_예외")
    @Transactional
    void THROW_BAD_KEYWORD_EXCEPTION_WHEN_SAVE() throws Exception {

        // given
        InputStream keywordStream = getClass().getClassLoader().getResourceAsStream("json/invalidKeywordResponse.json");
        String invalidKeywordResponse = IOUtils.toString(keywordStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(invalidKeywordResponse)
                ));


        SubjectRequest subjectRequest = new SubjectRequest("아아");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(post("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("주제로 부적합한 키워드입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("부적절한_주제_수정_시_예외")
    @Transactional
    void THROW_BAD_KEYWORD_EXCEPTION_WHEN_UPDATE() throws Exception {

        // given
        saveSubject();
        InputStream keywordStream = getClass().getClassLoader().getResourceAsStream("json/invalidKeywordResponse.json");
        String invalidKeywordResponse = IOUtils.toString(keywordStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(invalidKeywordResponse)
                ));


        SubjectUpdateRequest subjectUpdateRequest = new SubjectUpdateRequest("아아");
        String json = objectMapper.writeValueAsString(subjectUpdateRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(patch("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("주제로 부적합한 키워드입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("빈_값_등록_시_예외_발생")
    void THROW_EXCEPTION_REQUEST_INVALID_SUBJECT() throws Exception {

        // given
        SubjectRequest subjectRequest = new SubjectRequest("");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);


        // expected
        mockMvc.perform(post("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.data[0].field").value("keyword"))
                .andExpect(jsonPath("$.data[0].message").value("값을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("OpenAI_서버_500_에러")
    void OpenAI_INTERNAL_SERVER_ERROR() throws Exception {

        // given
        stubFor(WireMock.post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withStatus(500)
                                .withHeader(AUTHORIZATION, APPLICATION_JSON_VALUE)
                ));

        SubjectRequest subjectRequest = new SubjectRequest("개발자 전망");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(post("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("요청에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("OpenAI_서버_타임아웃")
    void OpenAI_TIMEOUT_ERROR() throws Exception {

        // given
        stubFor(WireMock.post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withFixedDelay(60000)
                                .withHeader(AUTHORIZATION, APPLICATION_JSON_VALUE)
                ));

        SubjectRequest subjectRequest = new SubjectRequest("개발자 전망");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(post("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("연결에 실패했습니다."))
                .andDo(print());
    }
}
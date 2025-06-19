package dev.park.dailynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.park.dailynews.config.DailyTest;
import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.SubjectRequest;
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

import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DailyTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
        SubjectRequest subjectRequest = new SubjectRequest("AI 전망");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // when
        mockMvc.perform(post("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andDo(print());

        Subject result = subjectRepository.findSubjectByUser("test@mail.com");

        // then
        assertEquals("AI 전망", result.getKeyword());

    }
    
    @Test
    @DisplayName("Subject가_이미_등록되어_있으면_update")
    @Transactional
    void UPDATE_SUBJECT_WHEN_SUBJECT_ALREADY_REGISTERED() throws Exception {
        
        // given
        saveSubject();
        SubjectRequest subjectRequest = new SubjectRequest("비트코인 전망");
        String json = objectMapper.writeValueAsString(subjectRequest);
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // when
        mockMvc.perform(patch("/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andDo(print());

        Subject result = subjectRepository.findSubjectByUser("test@mail.com");

        // then
        assertEquals("비트코인 전망", result.getKeyword());
    }

    @Test
    @DisplayName("부적절한_값을_등록_시_예외_발생")
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
}
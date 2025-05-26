package dev.park.dailynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.park.dailynews.config.CustomTestExecutionListener;
import dev.park.dailynews.config.DailyTest;
import dev.park.dailynews.domain.news.Subject;
import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.exception.TokenNotFoundException;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisTokenRepository;
import dev.park.dailynews.repository.subject.SubjectRepository;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
    private RedisTokenRepository tokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void clean() {
        tokenRepository.deleteAll();
        User savedUser = saveUser();
        saveToken(savedUser);
    }

    User saveUser() {
        User user = User.builder()
                .email("test@mail.com")
                .nickname("테스터")
                .provider(NAVER)
                .build();

        return userRepository.save(user);
    }

    void saveToken(User savedUser) {

        UserContext userContext = new UserContext(savedUser);
        String accessToken = jwtUtils.generateAccessToken(userContext);
        String refreshToken = jwtUtils.generateRefreshToken(userContext);

        AuthToken authToken = AuthToken.builder()
                .uuid(userContext.getUuid())
                .ip("127.0.0.1")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userAgent("Mozilla/5.0")
                .email(userContext.getEmail())
                .build();

        tokenRepository.save(authToken);
    }

    void saveSubject() {

        User savedUser = userRepository.findByEmail("test@mail.com")
                .orElseThrow(UserNotFoundException::new);

        Subject subject = Subject.builder()
                .keyword("AI 전망")
                .build();
        savedUser.setSubject(subject);

        subjectRepository.save(subject);
    }

    @Test
    @DisplayName("Subject_등록_성공")
    void SUCCESS_REGISTER_SUBJECT() throws Exception {

        // given
        SubjectRequest subjectRequest = new SubjectRequest("AI 전망");
        String json = objectMapper.writeValueAsString(subjectRequest);
        AuthToken savedToken = tokenRepository.findByEmail("test@mail.com")
                .orElseThrow(TokenNotFoundException::new);

        // when
        mockMvc.perform(post("/register/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", savedToken.getAccessToken())
                        .header("User-Agent", "Mozilla/5.0")
                )
                .andDo(print());

        Subject result = subjectRepository.findByUserEmail("test@mail.com");
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
        AuthToken savedToken = tokenRepository.findByEmail("test@mail.com")
                .orElseThrow(TokenNotFoundException::new);

        // when
        mockMvc.perform(post("/register/subject")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", savedToken.getAccessToken())
                        .header("User-Agent", "Mozilla/5.0")
                )
                .andDo(print());

        Subject result = subjectRepository.findByUserEmail("test@mail.com");

        // then
        assertEquals("비트코인 전망", result.getKeyword());

    }
}
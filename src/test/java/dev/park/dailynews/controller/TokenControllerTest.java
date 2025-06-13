package dev.park.dailynews.controller;

import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.user.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    private static final String UUID = "test-uuid";

    private static final String USER_EMAIL = "test@mail.com";
    private static final String USER_NICKNAME = "테스터";
    private static final String SOCIAL_TOKEN = "test-social-token";

    void saveUser() {
        User user = User.builder()
                .email(USER_EMAIL)
                .nickname(USER_NICKNAME)
                .provider(NAVER)
                .build();

        userRepository.save(user);
    }


    UserContext createUserContext() {
        return UserContext.builder()
                .uuid(UUID)
                .email(USER_EMAIL)
                .socialToken(SOCIAL_TOKEN)
                .provider(NAVER)
                .build();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        saveUser();
    }

    @Test
    @DisplayName("토큰_재발급_요청이_오면_토큰_재발급")
    void REISSUE_TOKEN_WHEN_REQUEST() throws Exception {

        // given
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);
        String refreshToken = jwtUtils.generateRefreshToken(userContext);
        Cookie setCookie = new Cookie("refreshToken", refreshToken);

        // expected
        Thread.sleep(1000);
        mockMvc.perform(get("/token/reissue")
                        .header(AUTHORIZATION, accessToken)
                        .cookie(setCookie)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andDo(print());

        // then
//        assertNotEquals(savedToken.getAccessToken(), reissuedToken.getAccessToken());
//        assertNotEquals(savedToken.getRefreshToken(), reissuedToken.getRefreshToken());
//        assertEquals(savedToken.getUuid(), reissuedToken.getUuid());
//        assertEquals(savedToken.getIp(), reissuedToken.getIp());
//        assertEquals(savedToken.getUserAgent(), reissuedToken.getUserAgent());
//        assertEquals(savedToken.getEmail(), reissuedToken.getEmail());

    }

    @Test
    @DisplayName("재발급_요청시_refreshToken_만료_시_ExpiredTokenException_예외_발생")
    void GET_ExpiredTokenException_WHEN_REFRESH_TOKEN_EXPIRED() throws Exception {

        // given
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);
        String refreshToken = jwtUtils.generateRefreshToken(userContext);
        Cookie setCookie = new Cookie("refreshToken", refreshToken);

        // expected
        Thread.sleep(3000);
        mockMvc.perform(get("/token/reissue")
                        .header(AUTHORIZATION, accessToken)
                        .cookie(setCookie)
                )
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("expired_refreshToken"))
                .andDo(print());
    }

    @Test
    @DisplayName("재발급_요청시_잘못된_형식의_refreshToken_UnAuthorized_예외_발생")
    void GET_UnAuthorized_WHEN_NOT_INVALID_REFRESH_TOKEN() throws Exception {

        // given
        String refreshToken = "not-jwt-type";
        Cookie setCookie = new Cookie("refreshToken", refreshToken);

        // expected
        mockMvc.perform(get("/token/reissue")
                        .cookie(setCookie)
                )
                .andExpect(jsonPath("$.statusCode").value("ERROR"))
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                .andDo(print());
    }
}
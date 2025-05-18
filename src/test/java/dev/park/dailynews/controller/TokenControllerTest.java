package dev.park.dailynews.controller;

import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.exception.TokenNotFoundException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisTokenRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    private RedisTokenRepository tokenRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void clean() {
        tokenRepository.deleteAll();
    }


    @Test
    @DisplayName("토큰_재발급_요청이_오면_토큰_재발급")
    void REISSUE_TOKEN_WHEN_REQUEST() throws Exception {

        // given
        String uuid = UUID.randomUUID().toString();
        UserContext user = new UserContext("test@email.com", uuid);
        SessionContext session = new SessionContext("127.0.0.1", "Mozilla/5.0");
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        AuthToken authToken = AuthToken.builder()
                .uuid(uuid)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .ip(session.getIp())
                .userAgent(session.getUserAgent())
                .build();

        AuthToken savedToken = tokenRepository.save(authToken);
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        Thread.sleep(1000);
        // expected
        mockMvc.perform(get("/token/reissue")
                        .cookie(cookie)
                        .header("User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andDo(print());
        AuthToken reissuedToken = tokenRepository.findByUuid(savedToken.getUuid())
                .orElseThrow(TokenNotFoundException::new);

        // then
        assertNotEquals(savedToken.getAccessToken(), reissuedToken.getAccessToken());
        assertNotEquals(savedToken.getRefreshToken(), reissuedToken.getRefreshToken());
        assertEquals(savedToken.getUuid(), reissuedToken.getUuid());
        assertEquals(savedToken.getIp(), reissuedToken.getIp());
        assertEquals(savedToken.getUserAgent(), reissuedToken.getUserAgent());
        assertEquals(savedToken.getEmail(), reissuedToken.getEmail());

    }
}
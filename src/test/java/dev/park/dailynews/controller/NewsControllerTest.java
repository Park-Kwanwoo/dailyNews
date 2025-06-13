package dev.park.dailynews.controller;

import dev.park.dailynews.config.DailyTest;
import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.domain.news.NewsItem;
import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.exception.TokenNotFoundException;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.news.NewsRepository;
import dev.park.dailynews.repository.subject.SubjectRepository;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DailyTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private NewsRepository newsRepository;
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

    void saveSubject() {

        User savedUser = userRepository.findByEmail(USER_EMAIL)
                .orElseThrow(UserNotFoundException::new);

        Subject subject = Subject.builder()
                .keyword("LCK")
                .build();

        subject.setUser(savedUser);

        subjectRepository.save(subject);
    }

    void saveNewsList() {

        User savedUser = userRepository.findByEmail(USER_EMAIL)
                .orElseThrow(UserNotFoundException::new);

        List<News> newsList = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> {
                    News news = News.builder().title("테스트 뉴스" + i).build();
                    IntStream.rangeClosed(1, 3)
                            .forEach(j -> {
                                NewsItem newsItem = NewsItem.builder()
                                        .headline("헤드라인 " + j)
                                        .summary("요약 " + j)
                                        .source("출처 " + j)
                                        .sourceUrl("출처 url " + j)
                                        .build();

                                newsItem.setNews(news);
                            });

                    news.setUser(savedUser);
                    return news;
                })
                .toList();

        newsRepository.saveAll(newsList);
    }

    @BeforeEach
    void setUpAndClean() {
        userRepository.deleteAll();
        subjectRepository.deleteAll();
        newsRepository.deleteAll();
        saveUser();
        saveSubject();
        saveNewsList();
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
    @DisplayName("뉴스_토픽_리스트_조회하기")
    @Transactional
    void GET_NEWS_TOPIC_LIST() throws Exception {

        // given
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(get("/news?page=1&size=10")
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.items.length()").value(10))
                .andExpect(jsonPath("$.data.items").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("뉴스_아이템_조회")
    @Transactional
    void GET_NEWS_ITEMS() throws Exception {

        // given
        UserContext userContext = createUserContext();
        String accessToken = jwtUtils.generateAccessToken(userContext);

        // expected
        mockMvc.perform(get("/news/{newsId}", 1L)
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(3))
                .andDo(print());
    }
}

package dev.park.dailynews.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.park.dailynews.config.DailyTest;
import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.domain.news.NewsItem;
import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.exception.ExternalApiException;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.news.NewsRepository;
import dev.park.dailynews.repository.subject.SubjectRepository;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.park.dailynews.domain.social.SocialProvider.NAVER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DailyTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WireMockTest(httpPort = 8090)
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsController newsController;

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

    // 스케쥴링 테스트
    @Test
    @DisplayName("뉴스_요약_OPENAI_API_요청_성공")
    @Transactional
    void GENERATE_NEWS_SUMMARY_WITH_OPENAI() throws IOException {

        // given
        InputStream newsStream = getClass().getClassLoader().getResourceAsStream("json/newsSummaryResponse.json");
        String newsSummaryResponse = IOUtils.toString(newsStream, StandardCharsets.UTF_8);

        stubFor(WireMock.post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(newsSummaryResponse)
                ));

        // when
        newsController.generateNews();

        // then
        User savedUser = userRepository.findByEmail(USER_EMAIL).orElseThrow(UserNotFoundException::new);
        List<News> news = savedUser.getNews();
        for (News summaryNews : news) {
            if (summaryNews.getTitle().equals("LCK")) {
                NewsItem newsItem = summaryNews.getNewsItems().get(0);
                assertEquals("T1, 2025 LCK 서머 개막전서 DRX에 2-0 승리", newsItem.getHeadline());
                assertEquals("2025년 5월 25일, T1이 2025 LCK 서머 스플릿 개막전에서 DRX를 2대 0으로 완파했다. Faker(이상혁)는 경기 후 인터뷰에서 \"팀원들과의 호흡이 좋아졌고, 시즌 초반이지만 좋은 출발을 하게 되어 기쁘다\"고 밝혔다. LCK 사무국은 이번 시즌부터 경기 일정과 운영 방침에 일부 변화를 도입했다고 공식 발표했다.", newsItem.getSummary());
                assertEquals("OSEN", newsItem.getSource());
                assertEquals("https://osen.mt.co.kr/article/20250525123456", newsItem.getSourceUrl());
            }
        }
    }
}

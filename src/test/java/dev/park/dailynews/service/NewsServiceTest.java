package dev.park.dailynews.service;

import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.domain.news.NewsItem;
import dev.park.dailynews.domain.news.NewsParse;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.PagingRequest;
import dev.park.dailynews.dto.response.common.PagingResponse;
import dev.park.dailynews.dto.response.news.NewsDetailResponse;
import dev.park.dailynews.dto.response.news.NewsResponse;
import dev.park.dailynews.infra.openai.CustomOpenAIClient;
import dev.park.dailynews.repository.news.NewsRepository;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static dev.park.dailynews.domain.social.SocialProvider.KAKAO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsServiceTest {


    @InjectMocks
    private NewsService newsService;

    @Mock
    private CustomOpenAIClient client;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("뉴스_요약_저장")
    void test() {

        // given
        User user = User.builder()
                .email("")
                .build();


        // when
        newsService.issueAndStoreNews();
    }

    @Test
    @DisplayName("뉴스_생성_및_저장")
    void GENERATE_NEWS_SUMMARY_AND_SAVE() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .provider(KAKAO)
                .nickname("카카오")
                .build();

        Subject subject = Subject.builder()
                .keyword("LCK")
                .build();

        user.setSubject(subject);
        NewsParse mockNewsParse = mock(NewsParse.class);


        given(userRepository.findAllWithSubject()).willReturn(List.of(user));
        given(client.getSummarizedNews(subject.getKeyword())).willReturn(mockNewsParse);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        newsService.issueAndStoreNews();

        // then
        verify(newsRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("뉴스_페이징_조회")
    void PAGING_NEWS_LIST() {

        // given
        User mockUser = mock(User.class);
        News news = News.builder()
                .title("주제")
                .build();

        PagingRequest pagingRequest = new PagingRequest(1, 10);
        PageImpl<News> fakeNews = new PageImpl<>(List.of(news));

        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(mockUser));

        given(newsRepository.getPagingNewsList(any(PagingRequest.class), anyLong()))
                .willReturn(fakeNews);

        // when
        PagingResponse<NewsResponse> savedNews = newsService.getNews(pagingRequest, "fake@mail.com");

        // then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(newsRepository, times(1)).getPagingNewsList(any(PagingRequest.class), anyLong());

        assertEquals("주제", savedNews.getItems().get(0).getTitle());
        assertEquals(1, savedNews.getItems().size());
    }

    @Test
    @DisplayName("뉴스_아이템_조회")
    void GET_NEWS_ITEM() {

        // given
        User mockUser = mock(User.class);
        News news = News.builder()
                .title("주제")
                .build();

        IntStream.rangeClosed(1, 3)
                .forEach(i -> {
                    NewsItem newsItem = NewsItem.builder()
                            .headline("헤드라인 " + i)
                            .summary("요약 " + i)
                            .source("출처 " + i)
                            .sourceUrl("출처 url " + i)
                            .build();
                    newsItem.setNews(news);
                });

        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(mockUser));

        given(newsRepository.findNewsWithItemsByNewsIdAndUserId(anyLong(), anyLong()))
                .willReturn(news);

        // when
        NewsDetailResponse newsItems = newsService.getNewsItems("fake@mail.com", 0L);

        // then
        assertEquals("주제", newsItems.title());
        assertEquals("헤드라인 1", newsItems.items().get(0).headline());
        assertEquals("요약 2", newsItems.items().get(1).summary());
        assertEquals("출처 3", newsItems.items().get(2).source());

    }
}


package dev.park.dailynews.service;

import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.domain.news.NewsItem;
import dev.park.dailynews.domain.news.NewsParse;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.PagingRequest;
import dev.park.dailynews.dto.response.common.PagingResponse;
import dev.park.dailynews.dto.response.news.NewsDetailResponse;
import dev.park.dailynews.dto.response.news.NewsResponse;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.openai.CustomOpenAIClient;
import dev.park.dailynews.repository.news.NewsRepository;
import dev.park.dailynews.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final CustomOpenAIClient client;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void issueAndStoreNews() {

        List<User> users = userRepository.findAllWithSubject();
        for (User user : users) {
            try {
                createNews(user.getSubject().getKeyword(), user.getId());
            } catch (Exception e) {
                log.error("뉴스 생성 실패 = {}", user.getEmail(), e);
            }
        }
    }

    public void createNews(String keyword, Long userId) {
        NewsParse newsParse = client.post(keyword);

        User savedUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        News news = News.builder()
                .title(newsParse.topic())
                .build();

        news.setUser(savedUser);

        newsParse.news()
                .forEach(it -> {
                    NewsItem newsItem = NewsItem.from(it);
                    newsItem.setNews(news);
                });

        newsRepository.save(news);
    }

    public PagingResponse<NewsResponse> getNews(PagingRequest pagingRequest, String email) {

        User savedUser = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        PageImpl<News> newsResponsePage = newsRepository.getPagingNewsList(pagingRequest, savedUser.getId());

        return new PagingResponse<>(newsResponsePage, NewsResponse.class);
    }

    public NewsDetailResponse getNewsItems(String email, Long newsId) {

        User savedUser = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        Long userId = savedUser.getId();
        News newsWithItems = newsRepository.findNewsWithItemsByNewsIdAndUserId(newsId, userId);

        return NewsDetailResponse.from(newsWithItems);
    }

}

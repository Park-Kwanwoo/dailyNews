package dev.park.dailynews.service;

import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.domain.news.NewsItem;
import dev.park.dailynews.domain.news.NewsParse;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.news.NewsItemDto;
import dev.park.dailynews.dto.response.news.NewsDetailResponse;
import dev.park.dailynews.dto.response.news.NewsResponse;
import dev.park.dailynews.exception.InvalidUserInfoException;
import dev.park.dailynews.exception.NewsNotFoundException;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.openai.CustomOpenAIClient;
import dev.park.dailynews.repository.news.NewsRepository;
import dev.park.dailynews.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dev.park.dailynews.domain.news.QNews.news;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final CustomOpenAIClient client;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
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

    public List<NewsResponse> getNews(String email) {

        User savedUser = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return newsRepository.findNewsByUserId(savedUser.getId());
    }

    public NewsDetailResponse getNewsItems(String email, Long newsId) {

        User savedUser = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        News newsWithItems = newsRepository.findWithItemsBy(newsId);

        if (!savedUser.getId().equals(newsWithItems.getUser().getId())) {
            throw new InvalidUserInfoException();
        }

        return NewsDetailResponse.from(newsWithItems);
    }

}

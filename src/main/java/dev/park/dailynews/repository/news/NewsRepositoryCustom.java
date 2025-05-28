package dev.park.dailynews.repository.news;

import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.dto.response.news.NewsResponse;

import java.util.List;

public interface NewsRepositoryCustom {

    List<NewsResponse> findNewsByUserId(Long userId);

    News findWithItemsBy(Long id);
}

package dev.park.dailynews.dto.response.news;

import dev.park.dailynews.domain.news.News;

import java.util.List;

public record NewsDetailResponse(
        Long id,
        String title,
        List<NewsItemDto> items
) {

    public static NewsDetailResponse from(News news) {
        List<NewsItemDto> list = news.getNewsItems().stream()
                .map(NewsItemDto::from)
                .toList();

        return new NewsDetailResponse(news.getId(), news.getTitle(), list);
    }
}

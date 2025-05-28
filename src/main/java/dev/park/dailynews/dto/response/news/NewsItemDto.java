package dev.park.dailynews.dto.response.news;

import dev.park.dailynews.domain.news.NewsItem;

public record NewsItemDto(
        String headline,
        String summary,
        String source,
        String sourceUrl
) {

    public static NewsItemDto from(NewsItem newsItem) {
        return new NewsItemDto(newsItem.getHeadline(), newsItem.getSummary(), newsItem.getSource(), newsItem.getSourceUrl());
    }
}

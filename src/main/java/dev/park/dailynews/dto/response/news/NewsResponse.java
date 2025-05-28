package dev.park.dailynews.dto.response.news;

import dev.park.dailynews.domain.news.News;
import lombok.Getter;

@Getter
public class NewsResponse {

    private final Long id;
    private final String title;

    public NewsResponse(News news) {
        this.id = news.getId();
        this.title = news.getTitle();
    }
}

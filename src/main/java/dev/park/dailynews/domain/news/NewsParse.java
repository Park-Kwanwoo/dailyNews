package dev.park.dailynews.domain.news;

import java.util.List;

public record NewsParse(
        String topic,
        List<Items> news
) {

    public record Items(
            String headline,
            String summary,
            String source,
            String url
    ) {

    }
}

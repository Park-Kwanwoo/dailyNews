package dev.park.dailynews.domain.news;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class NewsItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String headline;

    private String summary;

    private String source;

    private String sourceUrl;

    @ManyToOne
    private News news;

    @Builder
    public NewsItem(String headline, String summary, String source, String sourceUrl) {
        this.headline = headline;
        this.summary = summary;
        this.source = source;
        this.sourceUrl = sourceUrl;
    }


    public static NewsItem from(NewsParse.Items items) {
        return NewsItem.builder()
                .headline(items.headline())
                .summary(items.summary())
                .source(items.source())
                .sourceUrl(items.url())
                .build();
    }

    public void setNews(News news) {
        this.news = news;
        news.addNewsItem(this);
    }
}

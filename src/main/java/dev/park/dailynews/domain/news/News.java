package dev.park.dailynews.domain.news;

import dev.park.dailynews.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
    private List<NewsItem> newsItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public News(String title) {
        this.title = title;
    }
    public void addNewsItem(NewsItem newsItem) {
        if (newsItem != null) {
            this.newsItems.add(newsItem);
        }
    }

    public void setUser(User user) {
        this.user = user;
        user.getNews().add(this);
    }
}

package dev.park.dailynews.domain.user;

import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.social.SocialProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity(name = "USERS")
@NoArgsConstructor(access = PROTECTED)
@Getter
@ToString(exclude = {"subject", "news"})
@EqualsAndHashCode(exclude = {"subject", "news"})
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickname;

    @Enumerated(value = STRING)
    private SocialProvider provider;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Subject subject;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<News> news = new ArrayList<>();

    @Builder
    public User(Long id, String email, String nickname, SocialProvider provider) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void addNews(News news) {
        this.news.add(news);
    }
}

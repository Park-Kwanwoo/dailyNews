package dev.park.dailynews.domain.user;

import dev.park.dailynews.domain.news.Subject;
import dev.park.dailynews.domain.social.SocialProvider;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity(name = "USERS")
@NoArgsConstructor(access = PROTECTED)
@Getter
@ToString(exclude = "subject")
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
}

package dev.park.dailynews.domain.news;

import dev.park.dailynews.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@ToString(exclude = "user")
public class Subject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String keyword;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;

    @Builder
    public Subject(String keyword) {
        this.keyword = keyword;
    }

    public void setUser(User user) {
        this.user = user;
        user.setSubject(this);
    }

    public void changeKeyword(String keyword) {
        this.keyword = keyword;
    }
}

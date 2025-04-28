package dev.park.dailynews.domain;

import dev.park.dailynews.oauth.domain.OAuthProvider;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity(name = "USERS")
@NoArgsConstructor(access = PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickname;

    @Enumerated(value = STRING)
    private OAuthProvider provider;

    @Builder
    public User(Long id, String email, String nickname, OAuthProvider provider) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
    }
}

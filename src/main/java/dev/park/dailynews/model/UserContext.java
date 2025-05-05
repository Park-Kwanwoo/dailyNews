package dev.park.dailynews.model;

import dev.park.dailynews.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserContext {

    private final String email;
    private final String nickname;
    private final String uuid;

    public UserContext(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.uuid = UUID.randomUUID().toString();
    }
}

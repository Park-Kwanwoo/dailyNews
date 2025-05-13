package dev.park.dailynews.model;

import dev.park.dailynews.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class UserContext {

    private final String email;
    private final String uuid;

    public UserContext(User user) {
        this.email = user.getEmail();
        this.uuid = UUID.randomUUID().toString();
    }
}

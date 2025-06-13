package dev.park.dailynews.model;

import dev.park.dailynews.domain.social.SocialProvider;
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
    private final String socialToken;
    private final SocialProvider provider;

    public static UserContext from(User user, String socialToken) {
        return UserContext.builder()
                .uuid(UUID.randomUUID().toString())
                .email(user.getEmail())
                .socialToken(socialToken)
                .provider(user.getProvider())
                .build();
    }
}

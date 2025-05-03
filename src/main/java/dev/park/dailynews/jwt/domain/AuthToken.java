package dev.park.dailynews.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@RedisHash(value = "authToken", timeToLive = 60 * 60 * 24)
public class AuthToken {

    @Id
    private UUID uuid;

    @Indexed
    private String accessToken;

    private String refreshToken;

    public String getUuid() {
        return uuid.toString();
    }

    public static AuthToken of(String accessToken, String refreshToken, UUID uuid) {
        return AuthToken.builder()
                .uuid(uuid)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }
}

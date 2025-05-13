package dev.park.dailynews.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@Builder
@RedisHash(value = "authToken", timeToLive = 60 * 60 * 24)
public class AuthToken {

    @Id
    private String uuid;

    @Indexed
    private String email;

    private String accessToken;

    private String refreshToken;

    private String ip;

    private String userAgent;
}

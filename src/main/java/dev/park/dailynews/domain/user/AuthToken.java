package dev.park.dailynews.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

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
}

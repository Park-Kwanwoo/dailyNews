package dev.park.dailynews.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "userSession", timeToLive = 60 * 30)
@Getter
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    private String uuid;

    @Indexed
    private String email;

    private String ipAddress;

    private String userAgent;

}

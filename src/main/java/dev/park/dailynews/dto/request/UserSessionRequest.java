package dev.park.dailynews.dto.request;

import dev.park.dailynews.domain.user.UserSession;
import dev.park.dailynews.model.UserContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserSessionRequest {

    private String ip;
    private String userAgent;

    public UserSession toSessionInfo(UserContext userContext) {

        return UserSession.builder()
                .uuid(userContext.getUuid())
                .email(userContext.getEmail())
                .ipAddress(this.ip)
                .userAgent(this.userAgent)
                .build();
    }
}

package dev.park.dailynews.dto.response.sosical;

import dev.park.dailynews.domain.social.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialUserResponse {

    private final String nickname;
    private final String email;
    private final String provider;


    public static SocialUserResponse from(String nickname, String email, SocialProvider provider) {
        return new SocialUserResponse(nickname, email, provider.name());
    }
}

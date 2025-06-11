package dev.park.dailynews.dto.response.sosical;

import dev.park.dailynews.domain.social.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLogoutParams {

    private final String token;
    private final SocialProvider provider;

    public static SocialLogoutParams from(String token, String provider) {
        return new SocialLogoutParams(token, SocialProvider.valueOf(provider));
    }
}

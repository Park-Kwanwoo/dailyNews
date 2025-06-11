package dev.park.dailynews.model;

import dev.park.dailynews.domain.social.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SocialUserInfoContext {

    private final String id;
    private final String socialToken;

    private final String email;
    private final String nickname;
    private final SocialProvider provider;

}

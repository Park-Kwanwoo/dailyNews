package dev.park.dailynews.dto.response.sosical;

import dev.park.dailynews.domain.social.SocialProvider;

public interface SocialLoginParams {
    SocialProvider getSocialProvider();

    String getCode();
}
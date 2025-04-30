package dev.park.dailynews.oauth.response;

import dev.park.dailynews.oauth.domain.OAuthProvider;

public interface OAuthLoginParams {
    OAuthProvider getOAuthProvider();

    String getCode();
}
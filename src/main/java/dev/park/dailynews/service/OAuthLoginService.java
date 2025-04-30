package dev.park.dailynews.service;

import dev.park.dailynews.domain.User;
import dev.park.dailynews.jwt.JwtTokenProvider;
import dev.park.dailynews.oauth.client.NaverClient;
import dev.park.dailynews.oauth.domain.OAuth2UserInfo;
import dev.park.dailynews.oauth.response.OAuthLoginParams;
import dev.park.dailynews.repository.UserRepository;
import dev.park.dailynews.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dev.park.dailynews.oauth.domain.OAuthProvider.KAKAO;
import static dev.park.dailynews.oauth.domain.OAuthProvider.NAVER;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthClientService clientService;

    public User findOrCreateUser(OAuth2UserInfo oAuth2UserInfo) {

        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElseGet(() -> createUser(oAuth2UserInfo));

        return user;
    }

    public User createUser(OAuth2UserInfo oAuth2UserInfo) {

        User user = User.builder()
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getNickname())
                .provider(oAuth2UserInfo.getProvider())
                .build();

        return userRepository.save(user);
    }

    public LoginResponse login(OAuthLoginParams params) {
        OAuth2UserInfo oAuth2UserInfo = clientService.request(params);
        User user = findOrCreateUser(oAuth2UserInfo);
        return LoginResponse.builder()
                .accessToken(jwtTokenProvider.generateAccessToken())
                .email(user.getEmail())
                .oAuthProvider(oAuth2UserInfo.getProvider())
                .build();
    }
}

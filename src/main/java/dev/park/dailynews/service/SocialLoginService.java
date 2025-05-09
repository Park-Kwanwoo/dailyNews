package dev.park.dailynews.service;

import dev.park.dailynews.domain.social.SocialUserInfo;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final UserRepository userRepository;
    private final SocialClientService clientService;

    public UserContext findOrCreateUser(SocialUserInfo socialUserInfo) {

        User user = userRepository.findByEmail(socialUserInfo.getEmail())
                .orElseGet(() -> createUser(socialUserInfo));

        return new UserContext(user);
    }

    public User createUser(SocialUserInfo socialUserInfo) {

        User user = User.builder()
                .email(socialUserInfo.getEmail())
                .nickname(socialUserInfo.getNickname())
                .provider(socialUserInfo.getProvider())
                .build();

        return userRepository.save(user);
    }

    public UserContext login(SocialLoginParams params) {
        SocialUserInfo socialUserInfo = clientService.request(params);
        return findOrCreateUser(socialUserInfo);
    }
}

package dev.park.dailynews.service;

import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLogoutParams;
import dev.park.dailynews.dto.response.token.TokenResponse;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.SocialUserInfoContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final UserRepository userRepository;
    private final SocialClientService clientService;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;

    public UserContext findOrCreateUser(SocialUserInfoContext userInfo) {

        userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> createUser(userInfo));

        return UserContext.from(userInfo);
    }

    public User createUser(SocialUserInfoContext socialUserInfo) {

        User user = User.builder()
                .email(socialUserInfo.getEmail())
                .nickname(socialUserInfo.getNickname())
                .provider(socialUserInfo.getProvider())
                .build();

        return userRepository.save(user);
    }

    public TokenResponse login(SocialLoginParams params) {

        SocialUserInfoContext userInfo = clientService.login(params);
        UserContext userContext = findOrCreateUser(userInfo);
        String accessToken = jwtUtils.generateAccessToken(userContext);
        String refreshToken = jwtUtils.generateRefreshToken(userContext);

        return TokenResponse.from(accessToken, refreshToken);
    }

    public void logout(String accessToken) {

        redisTemplate.opsForValue()
                .set(accessToken, "Blacklist", jwtUtils.extractExpiration(accessToken), TimeUnit.SECONDS);

        String socialToken = jwtUtils.extractSocialToken(accessToken);
        String provider = jwtUtils.extractProvider(accessToken);

        SocialLogoutParams params = SocialLogoutParams.from(socialToken, provider);
        clientService.logout(params);
    }
}

package dev.park.dailynews.service;

import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.common.jwt.JwtToken;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTokenRepository redisTokenRepository;
    private final JwtToken jwtToken;

    @Transactional
    public AuthToken findOrCreateToken(UserContext userContext) {
        return redisTokenRepository.findByEmail(userContext.getEmail())
                .orElseGet(() -> createToken(userContext));
    }

    public AuthToken createToken(UserContext userContext) {

        String accessToken = jwtToken.generateAccessToken(userContext);
        String refreshToken = jwtToken.generateRefreshToken(userContext);
        AuthToken authToken = AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(userContext.getEmail())
                .uuid(userContext.getUuid())
                .build();

        return redisTokenRepository.save(authToken);
    }
}

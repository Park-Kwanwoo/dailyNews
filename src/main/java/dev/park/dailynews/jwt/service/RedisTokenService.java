package dev.park.dailynews.jwt.service;

import dev.park.dailynews.domain.User;
import dev.park.dailynews.jwt.domain.AuthToken;
import dev.park.dailynews.jwt.domain.JwtTokenProvider;
import dev.park.dailynews.jwt.respository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthToken findOrCreateToken(User user) {

        return redisTokenRepository.findByEmail(user.getEmail())
                .orElseGet(() -> createToken(user));
    }

    public AuthToken createToken(User user) {

        String email = user.getEmail();

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        AuthToken authToken = AuthToken.of(accessToken, refreshToken, UUID.randomUUID());

        return redisTokenRepository.save(authToken);
    }
}

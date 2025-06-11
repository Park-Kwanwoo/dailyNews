package dev.park.dailynews.infra.auth.jwt;

import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.InvalidTokenException;
import dev.park.dailynews.exception.TokenBlacklistException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;

    public boolean validToken(String accessToken) {
        try {
            return this.isTokenValid(accessToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("expired_accessToken");
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    private boolean isTokenValid(String token) {

        boolean validIssuer = jwtUtils.isValidIssuer(token);
        boolean tokenBlacklist = isTokenBlacklist(token);

        return validIssuer && tokenBlacklist;

    }

    private boolean isTokenBlacklist(String token) {

        if (stringRedisTemplate.opsForValue().get(token) != null) {
            throw new TokenBlacklistException();
        }

        return true;
    }
}

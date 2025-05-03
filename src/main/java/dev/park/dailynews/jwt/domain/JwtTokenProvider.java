package dev.park.dailynews.jwt.domain;

import dev.park.dailynews.exception.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
@AllArgsConstructor
@Getter
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private static final String TOKEN_PREFIX = "Bearer ";

    public SecretKey getSecretKey() {
        byte[] bytes = this.jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }
    private String deletePrefix(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateAccessToken(String email) {
        return generateToken(jwtProperties.accessExpirationTime(), email);
    }

    public String generateRefreshToken(String email) {
        return generateToken(jwtProperties.refreshExpirationTime(), email);
    }

    private String generateToken(long expirationTime, String email) {
        Date now = new Date();

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .issuer(jwtProperties.issuer())
                .signWith(getSecretKey())
                .compact();
    }

    public void verifyToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }

    public String extractToken(String token) throws InvalidTokenException {
        try {
            String accessToken = deletePrefix(token);
            return accessToken;
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException();
        }
    }
}

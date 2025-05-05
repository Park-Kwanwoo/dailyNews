package dev.park.dailynews.common.jwt;

import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
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
public class JwtToken {

    private final JwtProperties jwtProperties;

    public SecretKey getSecretKey() {
        byte[] bytes = this.jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(UserContext userContext) {
        return generateToken(jwtProperties.accessExpirationTime(), userContext.getEmail(), userContext.getUuid());
    }

    public String generateRefreshToken(UserContext userContext) {
        return generateToken(jwtProperties.refreshExpirationTime(), userContext.getEmail(), userContext.getUuid());
    }

    private String generateToken(long expirationTime, String email, String uuid) {
        Date now = new Date();

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .claim("uuid", uuid)
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
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("만료된 토큰입니다.");
        } catch (JwtException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }
}

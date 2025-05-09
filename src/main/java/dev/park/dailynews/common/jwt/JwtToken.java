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

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.accessExpirationTime());

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .claim("uuid", userContext.getUuid())
                .subject(userContext.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .issuer(jwtProperties.issuer())
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(UserContext userContext) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.accessExpirationTime());

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .claim("uuid", userContext.getUuid())
                .issuedAt(now)
                .expiration(expiration)
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

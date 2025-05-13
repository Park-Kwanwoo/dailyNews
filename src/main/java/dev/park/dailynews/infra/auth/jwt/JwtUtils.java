package dev.park.dailynews.infra.auth.jwt;

import dev.park.dailynews.model.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;


@Component
@AllArgsConstructor
@Getter
public class JwtUtils {

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
        Date expiration = new Date(now.getTime() + jwtProperties.refreshExpirationTime());

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

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractSubject(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public String extractIssuer(String token) {
        return extractClaims(token, Claims::getIssuer);
    }

    public String extractUUID(String token) {
        return extractClaims(token, claims -> claims.get("uuid", String.class));
    }
}

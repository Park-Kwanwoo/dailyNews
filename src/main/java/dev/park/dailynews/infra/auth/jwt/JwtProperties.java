package dev.park.dailynews.infra.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secretKey,
                            long accessExpirationTime,
                            long refreshExpirationTime,
                            String issuer) {
}

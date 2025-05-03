package dev.park.dailynews.jwt.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secretKey,
                            long accessExpirationTime,
                            long refreshExpirationTime,
                            String issuer) {
}

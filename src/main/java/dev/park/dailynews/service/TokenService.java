package dev.park.dailynews.service;

import dev.park.dailynews.domain.user.AuthToken;
import dev.park.dailynews.dto.response.TokenResponse;
import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.TokenNotFoundException;
import dev.park.dailynews.exception.UnAuthorized;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.model.TokenContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RedisTokenRepository redisTokenRepository;
    private final JwtUtils jwtUtils;

    public TokenResponse findOrCreateToken(UserContext userContext, SessionContext sessionContext) {
        AuthToken authToken = redisTokenRepository.findByEmail(userContext.getEmail())
                .orElseGet(() -> createToken(userContext, sessionContext));

        return TokenResponse.from(authToken);
    }

    public AuthToken createToken(UserContext userContext, SessionContext sessionContext) {

        String accessToken = jwtUtils.generateAccessToken(userContext);
        String refreshToken = jwtUtils.generateRefreshToken(userContext);
        AuthToken authToken = AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .ip(sessionContext.getIp())
                .userAgent(sessionContext.getUserAgent())
                .email(userContext.getEmail())
                .uuid(userContext.getUuid())
                .build();

        return redisTokenRepository.save(authToken);
    }

    public TokenContext getToken(String uuid) {

        AuthToken savedToken = redisTokenRepository.findByUuid(uuid)
                .orElseThrow(TokenNotFoundException::new);

        return TokenContext.builder()
                .email(savedToken.getEmail())
                .accessToken(savedToken.getAccessToken())
                .refreshToken(savedToken.getRefreshToken())
                .ip(savedToken.getIp())
                .userAgent(savedToken.getUserAgent())
                .build();
    }

    public TokenResponse reissueToken(String refreshToken, SessionContext sessionContext) {

        try {
            String uuid = jwtUtils.extractUUID(refreshToken);
            AuthToken savedToken = redisTokenRepository.findByUuid(uuid)
                    .orElseThrow(TokenNotFoundException::new);

            UserContext userContext = UserContext.from(savedToken);

            AuthToken reissuedToken = createToken(userContext, sessionContext);

            return TokenResponse.from(reissuedToken);

        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (JwtException e) {
            throw new UnAuthorized();
        }
    }
}

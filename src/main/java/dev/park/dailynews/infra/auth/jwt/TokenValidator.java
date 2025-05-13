package dev.park.dailynews.infra.auth.jwt;

import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.InvalidTokenException;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.model.TokenContext;
import dev.park.dailynews.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final JwtUtils jwtUtils;
    private final TokenService tokenService;

    public boolean validToken(String accessToken, SessionContext session) {
        try {
            return this.isTokenInfoMatch(accessToken, session);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    private boolean isTokenInfoMatch(String token, SessionContext session) {

        String uuid = jwtUtils.extractUUID(token);
        String email = jwtUtils.extractUserEmail(token);
        String issuer = jwtUtils.extractIssuer(token);
        TokenContext savedToken = tokenService.get(uuid);

        return savedToken.getIp().equals(session.getIp()) &&
                savedToken.getUserAgent().equals(session.getUserAgent()) &&
                savedToken.getEmail().equals(email) &&
                issuer.equals(jwtUtils.getJwtProperties().issuer());
    }
}

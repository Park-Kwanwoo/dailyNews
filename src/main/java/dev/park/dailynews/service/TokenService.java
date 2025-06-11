package dev.park.dailynews.service;

import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.token.TokenResponse;
import dev.park.dailynews.exception.ExpiredTokenException;
import dev.park.dailynews.exception.UnAuthorized;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.infra.auth.jwt.JwtUtils;
import dev.park.dailynews.model.TokenContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.user.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public TokenResponse reissueToken(String refreshToken) {

        try {
            String subject = jwtUtils.extractSubject(refreshToken);
            String socialToken = jwtUtils.extractSocialToken(refreshToken);

            User savedUser = userRepository.findByEmail(subject)
                    .orElseThrow(UserNotFoundException::new);

            UserContext userContext = UserContext.builder()
                    .uuid(UUID.randomUUID().toString())
                    .email(savedUser.getEmail())
                    .socialToken(socialToken)
                    .provider(savedUser.getProvider())
                    .build();

            String reissuedAccessToken = jwtUtils.generateAccessToken(userContext);
            String reissuedRefreshToken = jwtUtils.generateRefreshToken(userContext);

            return TokenResponse.from(reissuedAccessToken, reissuedRefreshToken);

        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("expired_refreshToken");
        } catch (JwtException e) {
            throw new UnAuthorized();
        }
    }
}

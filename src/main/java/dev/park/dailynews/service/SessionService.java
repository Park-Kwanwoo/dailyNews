package dev.park.dailynews.service;

import dev.park.dailynews.common.session.SessionCrypto;
import dev.park.dailynews.domain.user.UserSession;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisSessionRepository;
import dev.park.dailynews.dto.request.UserSessionRequest;
import dev.park.dailynews.model.SessionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisSessionRepository redisSessionRepository;
    private final SessionCrypto sessionCrypto;

    public String findOrCreateSession(UserContext userContext,
                                         UserSessionRequest userSessionRequest) throws Exception {

        UserSession session = redisSessionRepository.findByEmail(userContext.getEmail())
                .orElseGet(() -> saveSession(userContext, userSessionRequest));

        String encodedSessionId = sessionCrypto.encode(session);
        return encodedSessionId;
    }

    public UserSession saveSession(UserContext userContext,
                                   UserSessionRequest userSessionRequest) {

        UserSession userSession = UserSession.builder()
                .uuid(userContext.getUuid())
                .email(userContext.getEmail())
                .ipAddress(userSessionRequest.getIp())
                .userAgent(userSessionRequest.getUserAgent())
                .build();

        return redisSessionRepository.save(userSession);
    }
}

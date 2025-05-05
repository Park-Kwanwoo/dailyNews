package dev.park.dailynews.service;

import dev.park.dailynews.domain.user.UserSession;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.RedisSessionRepository;
import dev.park.dailynews.dto.request.UserSessionRequest;
import dev.park.dailynews.model.SessionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisSessionRepository redisSessionRepository;

    public SessionContext findOrCreate(UserContext userContext,
                                       UserSessionRequest userSessionRequest) {

        UserSession userSession = userSessionRequest.toSessionInfo(userContext);
        UserSession session = redisSessionRepository.findByEmail(userSession.getEmail())
                .orElseGet(() -> saveSession(userSession));

        return new SessionContext(session.getUuid());
    }

    public UserSession saveSession(UserSession userSession) {
        return redisSessionRepository.save(userSession);
    }
}

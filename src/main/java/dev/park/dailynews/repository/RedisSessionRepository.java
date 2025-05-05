package dev.park.dailynews.repository;

import dev.park.dailynews.domain.user.UserSession;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisSessionRepository extends CrudRepository<UserSession, String> {

    Optional<UserSession> findByEmail(String email);
}

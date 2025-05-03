package dev.park.dailynews.jwt.respository;

import dev.park.dailynews.jwt.domain.AuthToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisTokenRepository extends CrudRepository<AuthToken, String> {

    Optional<AuthToken> findByUuid(String uuid);
    Optional<AuthToken> findByEmail(String email);
}

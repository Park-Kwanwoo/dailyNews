package dev.park.dailynews.repository.user;

import dev.park.dailynews.domain.user.User;

import java.util.List;

public interface UserRepositoryCustom {

    User findByEmailWithSubjectLeftJoin(String email);

    List<User> findAllWithSubject();
}

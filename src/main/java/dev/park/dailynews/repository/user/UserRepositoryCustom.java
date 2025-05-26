package dev.park.dailynews.repository.user;

import dev.park.dailynews.domain.user.User;

public interface UserRepositoryCustom {

    User findByEmailWithSubjectLeftJoin(String email);
}

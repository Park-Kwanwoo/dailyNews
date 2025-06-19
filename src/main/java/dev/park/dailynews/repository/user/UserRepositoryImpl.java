package dev.park.dailynews.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.park.dailynews.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dev.park.dailynews.domain.subject.QSubject.subject;
import static dev.park.dailynews.domain.user.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findAllWithSubject() {

        return jpaQueryFactory.selectFrom(user)
                .innerJoin(user.subject, subject)
                .fetch();
    }
}

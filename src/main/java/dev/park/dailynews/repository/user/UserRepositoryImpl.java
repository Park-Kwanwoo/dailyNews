package dev.park.dailynews.repository.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.park.dailynews.domain.news.QSubject;
import dev.park.dailynews.domain.user.QUser;
import dev.park.dailynews.domain.user.User;
import lombok.RequiredArgsConstructor;

import static dev.park.dailynews.domain.news.QSubject.subject;
import static dev.park.dailynews.domain.user.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public User findByEmailWithSubjectLeftJoin(String email) {

        User savedUser = jpaQueryFactory.selectFrom(user)
                .leftJoin(user.subject, subject)
                .fetchJoin()
                .where(user.email.eq(email))
                .fetchOne();

        return savedUser;
    }
}

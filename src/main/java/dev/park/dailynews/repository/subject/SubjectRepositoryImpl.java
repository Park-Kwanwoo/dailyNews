package dev.park.dailynews.repository.subject;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.park.dailynews.domain.subject.Subject;
import lombok.RequiredArgsConstructor;

import static dev.park.dailynews.domain.subject.QSubject.subject;
import static dev.park.dailynews.domain.user.QUser.user;

@RequiredArgsConstructor
public class SubjectRepositoryImpl implements SubjectRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Subject findByUserEmail(String email) {

        return jpaQueryFactory.select(subject)
                .from(subject)
                .innerJoin(user)
                .on(subject.user.email.eq(user.email))
                .fetchFirst();

    }
}

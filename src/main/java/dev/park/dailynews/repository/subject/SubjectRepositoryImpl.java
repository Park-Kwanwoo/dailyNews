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
    public Subject findSubjectByUser(String email) {

        return jpaQueryFactory.selectFrom(subject)
                .innerJoin(subject.user, user)
                .fetchJoin()
                .where(user.email.eq(email))
                .fetchOne();

    }
}

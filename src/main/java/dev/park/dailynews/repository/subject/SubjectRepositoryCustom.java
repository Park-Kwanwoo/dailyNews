package dev.park.dailynews.repository.subject;

import dev.park.dailynews.domain.subject.Subject;

public interface SubjectRepositoryCustom {

    Subject findByUserEmail(String email);
}

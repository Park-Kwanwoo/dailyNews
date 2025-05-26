package dev.park.dailynews.repository.subject;

import dev.park.dailynews.domain.news.Subject;

public interface SubjectRepositoryCustom {

    Subject findByUserEmail(String email);
}

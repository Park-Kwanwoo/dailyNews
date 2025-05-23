package dev.park.dailynews.repository;

import dev.park.dailynews.domain.news.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubjectRepository extends JpaRepository<Subject, Long>, SubjectRepositoryCustom {

    boolean existsSubjectByUserEmail(String email);

    Optional<Subject> findSubjectByUserEmail(String email);
}

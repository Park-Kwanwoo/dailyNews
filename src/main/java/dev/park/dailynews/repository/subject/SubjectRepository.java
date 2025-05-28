package dev.park.dailynews.repository.subject;

import dev.park.dailynews.domain.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long>, SubjectRepositoryCustom {

    boolean existsSubjectByUserEmail(String email);

    Optional<Subject> findSubjectByUserEmail(String email);
}

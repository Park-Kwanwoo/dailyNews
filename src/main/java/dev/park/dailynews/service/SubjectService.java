package dev.park.dailynews.service;

import dev.park.dailynews.domain.news.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.response.anthropic.SubjectResponse;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.repository.SubjectRepository;
import dev.park.dailynews.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public void register(SubjectRequest subjectRequest, LoginUserContext user) {

        boolean exist = subjectRepository.existsSubjectByUserEmail(user.getEmail());

        if (exist) {
            update(subjectRequest, user);
        } else {
            save(subjectRequest, user);
        }
    }

    public void save(SubjectRequest subjectRequest, LoginUserContext user) {

        User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(UserNotFoundException::new);

        Subject subject = Subject.builder()
                .keyword(subjectRequest.getKeyword())
                .build();

        subject.setUser(savedUser);
        subjectRepository.save(subject);
    }

    public void update(SubjectRequest subjectRequest, LoginUserContext user) {

        Subject savedSubject = subjectRepository.findByUserEmail(user.getEmail());

        if (savedSubject != null) {
            savedSubject.changeKeyword(subjectRequest.getKeyword());
        }
    }

    public SubjectResponse getSubject(LoginUserContext user) {

        Subject subject = subjectRepository.findSubjectByUserEmail(user.getEmail())
                .orElseGet(() -> new Subject(""));

        return SubjectResponse.from(subject.getKeyword());
    }
}

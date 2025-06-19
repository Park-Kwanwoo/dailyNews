package dev.park.dailynews.service;

import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.response.subject.SubjectResponse;
import dev.park.dailynews.exception.UserNotFoundException;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.repository.subject.SubjectRepository;
import dev.park.dailynews.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(SubjectRequest subjectRequest, LoginUserContext userContext) {

        User savedUser = userRepository.findByEmail(userContext.getEmail())
                .orElseThrow(UserNotFoundException::new);

        Subject subject = Subject.builder()
                .keyword(subjectRequest.getKeyword())
                .build();

        subject.setUser(savedUser);
        subjectRepository.save(subject);
    }

    @Transactional
    public SubjectResponse update(SubjectRequest subjectRequest, LoginUserContext userContext) {

        Subject savedSubject = subjectRepository.findSubjectByUser(userContext.getEmail());

        if (savedSubject != null) {
            savedSubject.changeKeyword(subjectRequest.getKeyword());
        }

        return new SubjectResponse(subjectRequest.getKeyword());
    }

    public SubjectResponse getSubject(LoginUserContext userContext) {

        Subject subject = subjectRepository.findSubjectByUser(userContext.getEmail());
        return subject == null ? new SubjectResponse("") : SubjectResponse.from(subject.getKeyword());
    }
}

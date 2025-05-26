package dev.park.dailynews.service;

import dev.park.dailynews.domain.news.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.repository.subject.SubjectRepository;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @InjectMocks
    private SubjectService subjectService;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("등록된_주제가_없을때_새로운_주제_등록_성공")
    void REGISTER_SUBJECT() throws Exception {

        // given
        SubjectRequest subjectRequest = new SubjectRequest("AI 전망");
        LoginUserContext user = new LoginUserContext("test@mail.com");
        User savedUser = User.builder().email(user.getEmail()).build();

        given(subjectRepository.existsSubjectByUserEmail(user.getEmail())).willReturn(false);
        given(userRepository.findByEmailWithSubjectLeftJoin(user.getEmail())).willReturn(savedUser);

        // when
        subjectService.register(subjectRequest, user);

        // then
        assertEquals("AI 전망", savedUser.getSubject().getKeyword());

    }

    @Test
    @DisplayName("등록된_주제가_있을_경우_주제_업데이트")
    void UPDATE_SUBJECT() throws Exception {

        // given
        LoginUserContext user = new LoginUserContext("test@mail.com");
        SubjectRequest subjectRequest = new SubjectRequest("비트코인 전망");
        Subject savedSubject = Subject.builder().keyword("AI 전망").build();
        given(subjectRepository.existsSubjectByUserEmail(user.getEmail())).willReturn(true);
        given(subjectRepository.findByUserEmail(user.getEmail())).willReturn(savedSubject);

        // when
        subjectService.register(subjectRequest, user);

        // then
        assertEquals("비트코인 전망", savedSubject.getKeyword());

    }
}
package dev.park.dailynews.service;

import dev.park.dailynews.domain.subject.Subject;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.response.subject.SubjectResponse;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.repository.subject.SubjectRepository;
import dev.park.dailynews.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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
    void REGISTER_SUBJECT() {

        // given
        SubjectRequest subjectRequest = new SubjectRequest("AI 전망");
        LoginUserContext loginUserContext = new LoginUserContext("test@mail.com");
        User mockUser = mock(User.class);

        given(subjectRepository.existsSubjectByUserEmail(argThat(s ->  s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")))).willReturn(false);
        given(userRepository.findByEmailWithSubjectLeftJoin(argThat(s ->  s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")))).willReturn(mockUser);

        // when
        subjectService.register(subjectRequest, loginUserContext);

        // then
        verify(subjectRepository, times(1)).existsSubjectByUserEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")));
        verify(userRepository, times(1)).findByEmailWithSubjectLeftJoin(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")));

    }

    @Test
    @DisplayName("등록된_주제가_있을_경우_주제_업데이트")
    void UPDATE_SUBJECT() {

        // given
        SubjectRequest subjectRequest = new SubjectRequest("비트코인 전망");
        LoginUserContext loginUserContext = new LoginUserContext("test@mail.com");
        Subject savedSubject = Subject.builder().keyword("AI 전망").build();
        given(subjectRepository.existsSubjectByUserEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")))).willReturn(true);
        given(subjectRepository.findSubjectWithUserByEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")))).willReturn(savedSubject);

        // when
        subjectService.register(subjectRequest, loginUserContext);

        // then
        verify(subjectRepository, times(1)).existsSubjectByUserEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")));
        verify(subjectRepository, times(1)).findSubjectWithUserByEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")));

        assertEquals("비트코인 전망", savedSubject.getKeyword());

    }

    @Test
    @DisplayName("저장된_주제_조회_시_주제_응답")
    void RESPONSE_SUBJECT() {

        // given
        Subject mockSubject = Subject.builder().keyword("비트코인 전망").build();
        LoginUserContext userContext = new LoginUserContext("test@mail.com");
        given(subjectRepository.findSubjectWithUserByEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$"))))
                .willReturn(mockSubject);

        // when
        SubjectResponse subjectResponse = subjectService.getSubject(userContext);

        // then
        assertEquals("비트코인 전망", subjectResponse.keyword());
        verify(subjectRepository, times(1)).findSubjectWithUserByEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")));
    }

    @Test
    @DisplayName("저장된_주제가_없을_때_빈_응답")
    void RESPONSE_EMPTY_SUBJECT() {

        // given
        given(subjectRepository.findSubjectWithUserByEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$"))))
                .willReturn(null);
        LoginUserContext userContext = new LoginUserContext("test@mail.com");

        // when
        SubjectResponse subjectResponse = subjectService.getSubject(userContext);

        // then
        assertEquals("", subjectResponse.keyword());
        verify(subjectRepository, times(1)).findSubjectWithUserByEmail(argThat(s -> s != null && s.matches("^[^@]+@[^@]+\\.[^@]+$")));
    }
}
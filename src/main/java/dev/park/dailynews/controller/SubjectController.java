package dev.park.dailynews.controller;

import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.response.common.ApiResponse;
import dev.park.dailynews.dto.response.subject.SubjectResponse;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("/register/subject")
    public ApiResponse<?> registerSubject(@RequestBody SubjectRequest subject, LoginUserContext user) {
        subjectService.register(subject, user);
        return ApiResponse.successWithNoContent();
    }

    @GetMapping("/subject")
    public ApiResponse<SubjectResponse> getSubjects(LoginUserContext user) {

        SubjectResponse subject = subjectService.getSubject(user);
        return ApiResponse.successWithContent(subject);
    }
}

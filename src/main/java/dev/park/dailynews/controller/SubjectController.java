package dev.park.dailynews.controller;

import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.request.SubjectUpdateRequest;
import dev.park.dailynews.dto.response.common.ApiResponse;
import dev.park.dailynews.dto.response.subject.SubjectResponse;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("/subject")
    public ApiResponse<?> save(@RequestBody @Valid SubjectRequest subject, LoginUserContext user) {
        subjectService.save(subject, user);
        return ApiResponse.successWithNoContent();
    }

    @GetMapping("/subject")
    public ApiResponse<SubjectResponse> get(LoginUserContext userContext) {

        SubjectResponse subjectResponse = subjectService.getSubject(userContext);
        return ApiResponse.successWithContent(subjectResponse);
    }

    @PatchMapping("/subject")
    public ApiResponse<SubjectResponse> update(@RequestBody @Valid SubjectUpdateRequest subjectUpdateRequest, LoginUserContext userContext) {
        SubjectResponse subjectResponse = subjectService.update(subjectUpdateRequest, userContext);
        return ApiResponse.successWithContent(subjectResponse);
    }
}

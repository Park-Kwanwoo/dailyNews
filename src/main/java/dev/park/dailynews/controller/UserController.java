package dev.park.dailynews.controller;

import dev.park.dailynews.domain.news.Subject;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.response.ApiResponse;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.service.NewsService;
import dev.park.dailynews.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final NewsService newsService;


    @GetMapping("/mypage")
    public ApiResponse<Set<Subject>> getSubjects(LoginUserContext user) {
        Set<Subject> subjects = newsService.getSubjects(user);
        return ApiResponse.successWithContent(subjects);
    }

    @PostMapping("/register/subject")
    public void registerSubject(@RequestBody SubjectRequest subject, LoginUserContext user) {
        newsService.register(subject, user);
    }
}

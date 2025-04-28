package dev.park.dailynews.controller;

import dev.park.dailynews.response.ApiResponse;
import dev.park.dailynews.response.LoginResponse;
import dev.park.dailynews.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthLoginService OAuthLoginService;

    @GetMapping("/login/kakao")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestParam String code) {
        LoginResponse loginResponse = OAuthLoginService.login(code);
        return new ResponseEntity<>(ApiResponse.successWithContent(loginResponse), HttpStatus.OK);
    }

}

package dev.park.dailynews.controller;

import dev.park.dailynews.oauth.response.KakaoLoginParams;
import dev.park.dailynews.oauth.response.NaverLoginParams;
import dev.park.dailynews.response.ApiResponse;
import dev.park.dailynews.response.LoginResponse;
import dev.park.dailynews.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthLoginService OAuthLoginService;

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(@RequestBody KakaoLoginParams params) {
        LoginResponse loginResponse = OAuthLoginService.login(params);
        return new ResponseEntity<>(ApiResponse.successWithContent(loginResponse), HttpStatus.OK);
    }

    @PostMapping("/login/naver")
    public ResponseEntity<ApiResponse<LoginResponse>> naverLogin(@RequestBody NaverLoginParams params) {
        LoginResponse loginResponse = OAuthLoginService.login(params);
        return new ResponseEntity<>(ApiResponse.successWithContent(loginResponse), HttpStatus.OK);
    }
}

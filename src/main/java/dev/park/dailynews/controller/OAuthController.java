package dev.park.dailynews.controller;

import dev.park.dailynews.oauth.response.KakaoLoginParams;
import dev.park.dailynews.oauth.response.NaverLoginParams;
import dev.park.dailynews.response.ApiResponse;
import dev.park.dailynews.response.LoginResponse;
import dev.park.dailynews.service.OAuthLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthLoginService OAuthLoginService;

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(@RequestBody KakaoLoginParams params, HttpServletResponse res) {
        LoginResponse loginResponse = OAuthLoginService.login(params);
        res.addHeader(SET_COOKIE, setCookie("uuid", loginResponse.getUuid()));
        res.addHeader(SET_COOKIE, setCookie("accessToken", loginResponse.getAccessToken()));
        return new ResponseEntity<>(ApiResponse.successWithContent(loginResponse), HttpStatus.OK);
    }

    @PostMapping("/login/naver")
    public ResponseEntity<ApiResponse<LoginResponse>> naverLogin(@RequestBody NaverLoginParams params, HttpServletResponse res) {
        LoginResponse loginResponse = OAuthLoginService.login(params);
        return new ResponseEntity<>(ApiResponse.successWithContent(loginResponse), HttpStatus.OK);
    }

    private String setCookie(String name, String value) {

        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build()
                .toString();
    }
}

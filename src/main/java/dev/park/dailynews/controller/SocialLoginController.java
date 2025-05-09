package dev.park.dailynews.controller;

import dev.park.dailynews.dto.request.UserSessionRequest;
import dev.park.dailynews.dto.response.ApiResponse;
import dev.park.dailynews.dto.response.LoginResponse;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.NaverLoginParams;
import dev.park.dailynews.service.TokenSessionService;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.service.SocialLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialLoginController {

    private final SocialLoginService SocialLoginService;
    private final TokenSessionService tokenSessionService;

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(@RequestBody KakaoLoginParams params,
                                                                 HttpServletResponse res,
                                                                 UserSessionRequest userSessionRequest) throws Exception {

        UserContext userContext = SocialLoginService.login(params);
        LoginResponse loginResponse = tokenSessionService.issueSessionAndToken(userContext, userSessionRequest);
        res.addHeader(SET_COOKIE, setCookie("DN_AUT", loginResponse.getAccessToken()));
        res.addHeader(SET_COOKIE, setCookie("DN_SES", loginResponse.getSessionId()));
        return new ResponseEntity<>(ApiResponse.successWithContent(loginResponse), HttpStatus.OK);
    }

    @PostMapping("/login/naver")
    public ResponseEntity<ApiResponse<LoginResponse>> naverLogin(@RequestBody NaverLoginParams params,
                                                                 HttpServletResponse res,
                                                                 UserSessionRequest userSessionRequest) throws Exception {

        UserContext userContext = SocialLoginService.login(params);
        LoginResponse loginResponse = tokenSessionService.issueSessionAndToken(userContext, userSessionRequest);
        res.addHeader(SET_COOKIE, setCookie("DN_AUT", loginResponse.getAccessToken()));
        res.addHeader(SET_COOKIE, setCookie("DN_SES", loginResponse.getSessionId()));
        return new ResponseEntity<>(ApiResponse.successWithContent(loginResponse), HttpStatus.OK);
    }

    @GetMapping("/auth/test")
    public String AuthTest() {
        return "Hi, SUCCESS";
    }

    private String setCookie(String name, String value) {

        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .build()
                .toString();
    }
}

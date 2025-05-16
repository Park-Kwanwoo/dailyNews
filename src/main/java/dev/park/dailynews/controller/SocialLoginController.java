package dev.park.dailynews.controller;

import dev.park.dailynews.common.CookieUtils;
import dev.park.dailynews.dto.response.ApiResponse;
import dev.park.dailynews.dto.response.TokenResponse;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.NaverLoginParams;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.service.SocialLoginService;
import dev.park.dailynews.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialLoginController {

    private final SocialLoginService SocialLoginService;
    private final TokenService tokenService;

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<Void>> kakaoLogin(@RequestBody KakaoLoginParams params,
                                                                 HttpServletResponse response,
                                                                 SessionContext sessionContext) {

        UserContext userContext = SocialLoginService.login(params);
        TokenResponse token = tokenService.findOrCreateToken(userContext, sessionContext);
        CookieUtils.setCookie(response, token.getRefreshToken());
        CookieUtils.setAuthorization(response, token.getAccessToken());
        return new ResponseEntity<>(ApiResponse.successWithNoContent(), HttpStatus.OK);
    }

    @PostMapping("/login/naver")
    public ResponseEntity<ApiResponse<Void>> naverLogin(@RequestBody NaverLoginParams params,
                                                                 HttpServletResponse response,
                                                                 SessionContext session) throws Exception {

        UserContext userContext = SocialLoginService.login(params);
        TokenResponse token = tokenService.findOrCreateToken(userContext, session);
        CookieUtils.setCookie(response, token.getRefreshToken());
        CookieUtils.setAuthorization(response, token.getAccessToken());
        return new ResponseEntity<>(ApiResponse.successWithNoContent(), HttpStatus.OK);
    }

    @GetMapping("/auth/test")
    public String AuthTest() {
        return "Hi, SUCCESS";
    }
}

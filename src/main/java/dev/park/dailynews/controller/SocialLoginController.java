package dev.park.dailynews.controller;

import dev.park.dailynews.common.CookieUtils;
import dev.park.dailynews.dto.response.common.ApiResponse;
import dev.park.dailynews.dto.response.token.TokenResponse;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.service.SocialLoginService;
import dev.park.dailynews.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialLoginController {

    private final SocialLoginService SocialLoginService;
    private final TokenService tokenService;

    @PostMapping("/social/login")
    public ResponseEntity<ApiResponse<Void>> socialLogin(@RequestBody SocialLoginParams params,
                                                                 HttpServletResponse response,
                                                                 SessionContext sessionContext) {

        UserContext userContext = SocialLoginService.login(params);
        TokenResponse token = tokenService.findOrCreateToken(userContext, sessionContext);
        CookieUtils.setCookie(response, token.getRefreshToken());
        CookieUtils.setAuthorization(response, token.getAccessToken());
        return new ResponseEntity<>(ApiResponse.successWithNoContent(), HttpStatus.OK);
    }
}

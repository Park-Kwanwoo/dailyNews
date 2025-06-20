package dev.park.dailynews.controller;

import dev.park.dailynews.common.CookieUtils;
import dev.park.dailynews.dto.response.common.ApiResponse;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialUserResponse;
import dev.park.dailynews.dto.response.token.TokenResponse;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.service.SocialAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialAuthController {

    private final SocialAuthService socialAuthService;

    @PostMapping("/social/login")
    public ApiResponse<Void> socialLogin(@RequestBody SocialLoginParams params,
                                                                 HttpServletResponse response) {

        TokenResponse token = socialAuthService.login(params);
        CookieUtils.setRefreshToken(response, token.getRefreshToken());
        CookieUtils.setAuthorization(response, token.getAccessToken());
        return ApiResponse.successWithNoContent();
    }

    @PostMapping("/social/logout")
    public ApiResponse<Void> socialLogout(@RequestAttribute("accessToken") String accessToken) {
        socialAuthService.logout(accessToken);
        return ApiResponse.successWithNoContent();
    }

    @GetMapping("/social/info")
    public ApiResponse<SocialUserResponse> getUserInfo(LoginUserContext userContext) {
        SocialUserResponse socialUserResponse = socialAuthService.getUserInfo(userContext);
        return ApiResponse.successWithContent(socialUserResponse);
    }
}

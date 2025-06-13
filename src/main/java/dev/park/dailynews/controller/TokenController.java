package dev.park.dailynews.controller;

import dev.park.dailynews.common.CookieUtils;
import dev.park.dailynews.dto.response.common.ApiResponse;
import dev.park.dailynews.dto.response.token.TokenResponse;
import dev.park.dailynews.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/token/reissue")
    public ApiResponse<?> reissueToken(HttpServletRequest request,
                                                          HttpServletResponse response) {

        String refreshToken = CookieUtils.extractCookieValue("refreshToken", request);
        TokenResponse reissuedToken = tokenService.reissueToken(refreshToken);
        CookieUtils.setAuthorization(response, reissuedToken.getAccessToken());
        CookieUtils.setRefreshToken(response, reissuedToken.getRefreshToken());

        return ApiResponse.successWithNoContent();

    }
}

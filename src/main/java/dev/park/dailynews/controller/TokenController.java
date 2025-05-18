package dev.park.dailynews.controller;

import dev.park.dailynews.common.CookieUtils;
import dev.park.dailynews.dto.response.ApiResponse;
import dev.park.dailynews.dto.response.TokenResponse;
import dev.park.dailynews.model.SessionContext;
import dev.park.dailynews.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/token/reissue")
    public ResponseEntity<ApiResponse<Void>> reissueToken(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          SessionContext session) {

        String refreshToken = CookieUtils.extractCookieValue("refreshToken", request);
        TokenResponse reissuedToken = tokenService.reissueToken(refreshToken, session);
        CookieUtils.setAuthorization(response, reissuedToken.getAccessToken());
        CookieUtils.setCookie(response, reissuedToken.getRefreshToken());

        return new ResponseEntity<>(ApiResponse.successWithNoContent(), HttpStatus.OK);
    }
}

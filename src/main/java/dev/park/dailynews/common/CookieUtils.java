package dev.park.dailynews.common;

import dev.park.dailynews.exception.CookieNotExistException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

public class CookieUtils {

    public static void setRefreshToken(HttpServletResponse response, String token) {
        response.addHeader(SET_COOKIE, setRefreshToken("refreshToken", token));
    }

    public static void setAccessToken(HttpServletResponse response, String token) {
        response.addHeader(SET_COOKIE, setRefreshToken("accessToken", token));
    }

    public static void setAuthorization(HttpServletResponse response, String token) {
        response.addHeader(AUTHORIZATION, token);
    }

    private static String setRefreshToken(String name, String value) {

        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .build()
                .toString();
    }

    public static String extractCookieValue(String value, HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(CookieNotExistException::new);

        String cookieValue = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(value))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(NullPointerException::new);

        return cookieValue;
    }

}

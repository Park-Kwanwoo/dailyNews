package dev.park.dailynews.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

public class CookieUtils {

    public static void setCookie(HttpServletResponse response, String token) {
        response.addHeader(SET_COOKIE, setCookie("refreshToken", token));
    }

    public static void setAuthorization(HttpServletResponse response, String token) {
        response.addHeader(AUTHORIZATION, token);
    }
    private static String setCookie(String name, String value) {

        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .build()
                .toString();
    }
}

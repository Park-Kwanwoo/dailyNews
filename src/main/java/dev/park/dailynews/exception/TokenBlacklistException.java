package dev.park.dailynews.exception;

public class TokenBlacklistException extends DailyNewsException {

    private static final String MESSAGE = "로그아웃된 토큰 정보입니다.";

    public TokenBlacklistException() {
        super(MESSAGE);
    }
}

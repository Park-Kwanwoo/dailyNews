package dev.park.dailynews.exception;

public class TokenNotFoundException extends DailyNewsException {

    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public TokenNotFoundException() {
        super(MESSAGE);
    }
}

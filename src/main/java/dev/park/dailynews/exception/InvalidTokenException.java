package dev.park.dailynews.exception;

public class InvalidTokenException extends DailyNewsException {

    private static final String MESSAGE = "유효하지 않은 토큰입니다.";
    public InvalidTokenException() {
        super(MESSAGE);
    }
}

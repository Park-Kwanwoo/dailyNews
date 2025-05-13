package dev.park.dailynews.exception;

public class ExpiredTokenException extends DailyNewsException {

    private static final String MESSAGE = "만료된 토큰입니다.";
    public ExpiredTokenException() {
        super(MESSAGE);
    }
}

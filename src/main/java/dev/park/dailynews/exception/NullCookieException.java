package dev.park.dailynews.exception;

public class NullCookieException extends DailyNewsException {

    private static final String MESSAGE = "쿠키값이 존재하지 않습니다.";

    public NullCookieException() {
        super(MESSAGE);
    }
}

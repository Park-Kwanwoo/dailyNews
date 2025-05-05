package dev.park.dailynews.exception;

public class ExpiredTokenException extends DailyNewsException {

    public ExpiredTokenException(String message) {
        super(message);
    }
}

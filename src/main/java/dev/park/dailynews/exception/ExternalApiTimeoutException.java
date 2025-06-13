package dev.park.dailynews.exception;

public class ExternalApiTimeoutException extends DailyNewsException {

    private static final String MESSAGE = "연결에 실패했습니다.";

    public ExternalApiTimeoutException() {
        super(MESSAGE);
    }
}

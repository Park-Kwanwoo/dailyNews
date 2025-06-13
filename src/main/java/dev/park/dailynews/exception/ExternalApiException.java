package dev.park.dailynews.exception;

public class ExternalApiException extends DailyNewsException {

    private static final String MESSAGE = "요청에 실패했습니다.";

    public ExternalApiException() {
        super(MESSAGE);
    }
}

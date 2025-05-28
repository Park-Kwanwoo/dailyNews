package dev.park.dailynews.exception;

public class InvalidSessionInfoException extends DailyNewsException {

    private static final String MESSAGE = "사용자 세션 정보가 일치하지 않습니다.";

    public InvalidSessionInfoException() {
        super(MESSAGE);
    }
}

package dev.park.dailynews.exception;

public class UnAuthorized extends DailyNewsException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public UnAuthorized() {
        super(MESSAGE);
    }
}

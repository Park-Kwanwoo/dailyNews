package dev.park.dailynews.exception;

public class NewsNotFoundException extends DailyNewsException {

    private static final String MESSAGE = "존재하지 않는 뉴스입니다.";
    public NewsNotFoundException() {
        super(MESSAGE);
    }
}

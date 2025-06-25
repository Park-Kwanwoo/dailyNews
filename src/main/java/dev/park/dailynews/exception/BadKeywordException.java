package dev.park.dailynews.exception;

public class BadKeywordException extends DailyNewsException {

    private static final String MESSAGE = "주제로 부적합한 키워드입니다.";

    public BadKeywordException() {
        super(MESSAGE);
    }
}

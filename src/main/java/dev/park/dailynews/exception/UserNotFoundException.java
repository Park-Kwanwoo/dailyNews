package dev.park.dailynews.exception;

public class UserNotFoundException extends DailyNewsException {

    private static final String MESSAGE = "존재하지 않는 회원입니다.";
    public UserNotFoundException() {
        super(MESSAGE);
    }
}

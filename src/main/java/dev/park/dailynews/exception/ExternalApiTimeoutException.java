package dev.park.dailynews.exception;

import org.springframework.web.client.ResourceAccessException;

public class ExternalApiTimeoutException extends ResourceAccessException {

    private static final String MESSAGE = "연결에 실패했습니다.";

    public ExternalApiTimeoutException() {
        super(MESSAGE);
    }
}

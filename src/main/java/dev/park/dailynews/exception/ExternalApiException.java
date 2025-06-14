package dev.park.dailynews.exception;

import org.springframework.web.client.RestClientException;

public class ExternalApiException extends RestClientException {

    private static final String MESSAGE = "요청에 실패했습니다.";

    public ExternalApiException() {
        super(MESSAGE);
    }
}

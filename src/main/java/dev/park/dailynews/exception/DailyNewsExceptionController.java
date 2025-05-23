package dev.park.dailynews.exception;

import dev.park.dailynews.dto.response.common.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DailyNewsExceptionController {

    @ExceptionHandler(value = DailyNewsException.class)
    public ApiResponse<?> catchException(DailyNewsException e) {
        return ApiResponse.error(e.getMessage());
    }
}

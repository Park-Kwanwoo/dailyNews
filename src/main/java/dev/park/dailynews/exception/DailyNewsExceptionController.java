package dev.park.dailynews.exception;

import dev.park.dailynews.dto.response.common.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DailyNewsExceptionController {

    @ExceptionHandler(value = DailyNewsException.class)
    public ApiResponse<?> customExceptionHandler(DailyNewsException e) {
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> validExceptionHandler(MethodArgumentNotValidException e) {
        return ApiResponse.errorWithBindingResult(e);
    }
}

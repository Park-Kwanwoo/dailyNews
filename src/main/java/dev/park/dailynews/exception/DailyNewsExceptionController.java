package dev.park.dailynews.exception;

import dev.park.dailynews.dto.response.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@Slf4j
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

    @ExceptionHandler(value = RestClientException.class)
    public ApiResponse<?> restTemplateExceptionHandler(RestClientException e) {
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public void restTemplateExceptionHandler(HttpRequestMethodNotSupportedException ex,
                                                       HttpServletRequest req) {
        log.error("Unsupported HTTP Method: {} {} from IP: {}", req.getMethod(),
                req.getRequestURI(),
                req.getRemoteAddr());
        log.error("{}", ex.getMessage());
    }
}

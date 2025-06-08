package dev.park.dailynews.dto.response.common;

import dev.park.dailynews.model.ExceptionContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";
    private static final String ERROR = "ERROR";

    private String statusCode;
    private String message;
    private T data;


    public static <T> ApiResponse<T> successWithNoContent() {
        return new ApiResponse<>(SUCCESS, null, null);
    }

    public static <T> ApiResponse<T> successWithContent(T data) {
        return new ApiResponse<>(SUCCESS, null, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ERROR, message, null);
    }

    public static ApiResponse<?> errorWithBindingResult(BindingResult e) {

        List<ExceptionContext> exceptions = new ArrayList<>();

        for (FieldError error : e.getFieldErrors()) {
            exceptions.add(ExceptionContext.from(error));
        }

        return new ApiResponse<>(ERROR, null, exceptions);
    }
}

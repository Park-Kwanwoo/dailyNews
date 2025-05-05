package dev.park.dailynews.dto.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";
    private static final String ERROR = "ERROR";

    private final String statusCode;
    private final T data;

    public ApiResponse(String statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> ApiResponse<T> successWithNoContent() {
        return new ApiResponse<>(SUCCESS, null);
    }

    public static <T> ApiResponse<T> successWithContent(T data) {
        return new ApiResponse<>(SUCCESS, data);
    }
}

package dev.park.dailynews.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@AllArgsConstructor
public class ExceptionContext {

    private String field;
    private String message;

    public static ExceptionContext from(FieldError fieldError) {
        return new ExceptionContext(fieldError.getField(), fieldError.getDefaultMessage());
    }
}

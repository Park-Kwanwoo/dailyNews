package dev.park.dailynews.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SubjectRequest {

    @NotBlank(message = "값을 입력해주세요.")
    private final String keyword;

    @JsonCreator
    public SubjectRequest(@JsonProperty("keyword") String keyword) {
        this.keyword = keyword;
    }
}

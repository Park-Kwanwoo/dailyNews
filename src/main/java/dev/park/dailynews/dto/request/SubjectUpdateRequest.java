package dev.park.dailynews.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SubjectUpdateRequest {

    @NotBlank(message = "값을 입력해주세요.")
    private final String keyword;

    @JsonCreator
    public SubjectUpdateRequest(@JsonProperty("keyword") String keyword) {
        this.keyword = keyword;
    }
}

package dev.park.dailynews.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SubjectRequest {

    @NotNull
    private final String keyword;

    @JsonCreator
    public SubjectRequest(@JsonProperty("keyword") String keyword) {
        this.keyword = keyword;
    }
}

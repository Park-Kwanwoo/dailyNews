package dev.park.dailynews.domain.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsResult(
       List<Output> output
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Output(
            String arguments
    ) {
    }
}

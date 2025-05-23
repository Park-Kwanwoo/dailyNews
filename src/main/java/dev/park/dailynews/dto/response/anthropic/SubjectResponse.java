package dev.park.dailynews.dto.response.anthropic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubjectResponse {

    private final String keyword;

    public static SubjectResponse from(String keyword) {
        return new SubjectResponse(keyword);
    }
}

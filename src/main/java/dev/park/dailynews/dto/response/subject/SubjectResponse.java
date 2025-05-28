package dev.park.dailynews.dto.response.subject;

public record SubjectResponse(
        String keyword
) {

    public static SubjectResponse from(String keyword) {
        return new SubjectResponse(keyword);
    }
}

package dev.park.dailynews.dto.response.sosical;

import dev.park.dailynews.domain.social.SocialProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginParams implements SocialLoginParams {

    @NotBlank
    private String code;

    @NotBlank
    private String state;

    @Override
    public SocialProvider getSocialProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}

package dev.park.dailynews.dto.response.sosical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.park.dailynews.domain.social.SocialProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoLoginParams implements SocialLoginParams {

    @NotBlank
    private String code;

    @Override
    public SocialProvider getSocialProvider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public String getCode() {
        return this.code;
    }

}

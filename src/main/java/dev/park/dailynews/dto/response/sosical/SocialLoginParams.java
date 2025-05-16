package dev.park.dailynews.dto.response.sosical;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.park.dailynews.domain.social.SocialProvider;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "provider"
)
@JsonSubTypes(
        value = {
                @JsonSubTypes.Type(value = KakaoLoginParams.class, name = "KAKAO"),
                @JsonSubTypes.Type(value = NaverLoginParams.class, name = "NAVER")
        }
)
public interface SocialLoginParams {
    SocialProvider getSocialProvider();

    String getCode();
}
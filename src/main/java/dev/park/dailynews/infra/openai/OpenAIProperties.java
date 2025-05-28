package dev.park.dailynews.infra.openai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
@RequiredArgsConstructor
@Getter
public class OpenAIProperties {

    private final String openaiKey;
    private final String openaiResponseUrl;
}

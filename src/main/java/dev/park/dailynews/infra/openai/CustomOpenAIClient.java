package dev.park.dailynews.infra.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.park.dailynews.domain.news.NewsParse;
import dev.park.dailynews.domain.news.NewsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class CustomOpenAIClient {

    private final OpenAIProperties openAIProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate rt;

    public CustomOpenAIClient(OpenAIProperties openAIProperties, ObjectMapper objectMapper, @Qualifier("aiRestTemplate") RestTemplate rt) {
        this.openAIProperties = openAIProperties;
        this.objectMapper = objectMapper;
        this.rt = rt;
    }

    public NewsParse post(String keyword) {
        NewsResult response = rt.postForObject(
                openAIProperties.getOpenaiResponseUrl(),
                request(keyword),
                NewsResult.class
        );

        String arguments = response.output().get(0).arguments();
        try {
            NewsParse newsParse = objectMapper.readValue(arguments, NewsParse.class);
            return newsParse;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private HttpEntity<Object> request(String keyword) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, "Bearer " + openAIProperties.getOpenaiKey());
        try {
            Resource resource = new ClassPathResource("OpenAI.json");
            InputStream inputStream = resource.getInputStream();
            ObjectNode objectNode = objectMapper.readTree(inputStream).deepCopy();
            objectNode.put("input", keyword);
            return new HttpEntity<>(objectNode, headers);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

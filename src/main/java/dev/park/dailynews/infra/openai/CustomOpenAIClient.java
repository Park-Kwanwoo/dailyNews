package dev.park.dailynews.infra.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.park.dailynews.domain.news.NewsParse;
import dev.park.dailynews.domain.news.NewsResult;
import dev.park.dailynews.domain.subject.KeywordParse;
import dev.park.dailynews.domain.subject.KeywordResult;
import dev.park.dailynews.exception.ExternalApiException;
import dev.park.dailynews.exception.ExternalApiTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    public NewsParse getSummarizedNews(String keyword) {

        try {
            NewsResult response = rt.postForObject(
                    openAIProperties.getOpenaiResponseUrl(),
                    buildNewsRequest(keyword),
                    NewsResult.class
            );

            String arguments = response.output().get(0).arguments();
            try {
                NewsParse newsParse = objectMapper.readValue(arguments, NewsParse.class);
                return newsParse;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            log.error("OpenAI API 연결 실패", e);
            throw new ExternalApiTimeoutException();
        } catch (RestClientException e) {
            log.error("OpenAI API 요청 실패", e);
            throw new ExternalApiException();
        }

    }

    private HttpEntity<Object> buildNewsRequest(String keyword) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, "Bearer " + openAIProperties.getOpenaiKey());
        Resource resource = new ClassPathResource("keywordAI.json");
        try (InputStream is = resource.getInputStream()) {
            ObjectNode objectNode = objectMapper.readTree(is).deepCopy();
            objectNode.put("input", keyword);
            return new HttpEntity<>(objectNode, headers);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public boolean validateKeyword(String keyword) {

        try {
            KeywordResult response = rt.postForObject(
                    openAIProperties.getOpenaiResponseUrl(),
                    buildKeywordValidateRequest(keyword),
                    KeywordResult.class
            );

            log.info("response = {}", response);
            String arguments = response.output().get(0).arguments();
            try {
                KeywordParse keywordParse = objectMapper.readValue(arguments, KeywordParse.class);
                return keywordParse.result().equals("O");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            log.error("OpenAI API 연결 실패", e);
            throw new ExternalApiTimeoutException();
        } catch (RestClientException e) {
            log.error("OpenAI API 요청 실패", e);
            throw new ExternalApiException();
        }
    }

    private HttpEntity<Object> buildKeywordValidateRequest(String keyword) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, "Bearer " + openAIProperties.getOpenaiKey());
        Resource resource = new ClassPathResource("keywordAI.json");
        try (InputStream is = resource.getInputStream()) {
            ObjectNode objectNode = objectMapper.readTree(is).deepCopy();
            objectNode.put("input", keyword);
            return new HttpEntity<>(objectNode, headers);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

package dev.park.dailynews.infra.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.park.dailynews.domain.news.NewsParse;
import dev.park.dailynews.domain.news.NewsResult;
import dev.park.dailynews.domain.subject.KeywordResult;
import dev.park.dailynews.exception.ExternalApiException;
import dev.park.dailynews.exception.ExternalApiTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOpenAIClientUnitTest {

    private CustomOpenAIClient client;
    private RestTemplate mockRestTemplate;

    private ObjectMapper objectMapper;

    static OpenAIProperties openAIProperties() {
        return new OpenAIProperties("test-api-key",
                "responseUrl");
    }

    @BeforeEach
    void setUp() {
        mockRestTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        client = new CustomOpenAIClient(openAIProperties(), objectMapper, mockRestTemplate);
    }

    @Test
    @DisplayName("뉴스_요약_요청_성공")
    void REQUEST_NEWS_SUMMARY_SUCCESS() {

        // given
        String fakeKeyword = "뉴스";
        NewsResult newsResult = new NewsResult(
                List.of(new NewsResult.Output(
                        """
                                {
                                    "topic": "LCK",
                                    "news": [
                                        {
                                            "headline": "headline1",
                                            "summary": "summary1",
                                            "source": "source1",
                                            "url":"url1"
                                        },
                                        {
                                            "headline": "headline2",
                                            "summary": "summary2",
                                            "source": "source2",
                                            "url":"url2"
                                        }
                                    ]
                                }"
                                """
                ))
        );

        given(mockRestTemplate.postForObject(
                eq("responseUrl"),
                any(),
                eq(NewsResult.class)))
                .willReturn(newsResult);


        // when
        NewsParse summarizedNews = client.getSummarizedNews(fakeKeyword);
        List<NewsParse.Items> news = summarizedNews.news();

        // then
        assertEquals(2, news.size());
        assertEquals("headline1", news.get(0).headline());
        assertEquals("summary1", news.get(0).summary());
        assertEquals("source1", news.get(0).source());
        assertEquals("url1", news.get(0).url());

        verify(mockRestTemplate, times(1)).postForObject(eq("responseUrl"), any(), eq(NewsResult.class));
    }

    @Test
    @DisplayName("적절한_주제_요청_응답")
    void RESPONSE_VALID_KEYWORD() {

        // given
        String fakeKeyword = "개발자 전망";
        KeywordResult keywordResult = new KeywordResult(
                List.of(new KeywordResult.Output(
                        "{\"result\": \"O\"}"
                ))
        );

        given(mockRestTemplate.postForObject(
                eq("responseUrl"),
                any(),
                eq(KeywordResult.class)))
                .willReturn(keywordResult);


        // when
        boolean isValid = client.validateKeyword(fakeKeyword);

        // then
        assertTrue(isValid);
        verify(mockRestTemplate, times(1)).postForObject(eq("responseUrl"), any(), eq(KeywordResult.class));
    }

    @Test
    @DisplayName("부적절한_주제_요청_응답")
    void RESPONSE_INVALID_KEYWORD() {

        // given
        String fakeKeyword = "개발자 전망";
        KeywordResult keywordResult = new KeywordResult(
                List.of(new KeywordResult.Output(
                        "{\"result\": \"X\"}"
                ))
        );

        given(mockRestTemplate.postForObject(
                eq("responseUrl"),
                any(),
                eq(KeywordResult.class)))
                .willReturn(keywordResult);


        // when
        boolean isValid = client.validateKeyword(fakeKeyword);

        // then
        assertFalse(isValid);
        verify(mockRestTemplate, times(1)).postForObject(eq("responseUrl"), any(), eq(KeywordResult.class));
    }

    @Test
    @DisplayName("뉴스_요약_요청_시_OpenAPI_서버_에러")
    void OPENAI_SERVER_ERROR_WHEN_REQUEST_NEWS_SUMMARY() {

        // given
        String fakeKeyword = "서버에러";
        given(mockRestTemplate.postForObject(
                eq("responseUrl"),
                any(),
                eq(NewsResult.class)))
                .willThrow(ExternalApiException.class);

        // when
        assertThatThrownBy(() -> client.getSummarizedNews(fakeKeyword))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }

    @Test
    @DisplayName("주제_검증_요청_시_OpenAPI_타임아웃")
    void OPENAI_TIMEOUT_WHEN_REQUEST_NEWS_SUMMARY() {

        // given
        String fakeKeyword = "타임아웃";
        given(mockRestTemplate.postForObject(
                eq("responseUrl"),
                any(),
                eq(NewsResult.class)))
                .willThrow(ExternalApiTimeoutException.class);

        // when
        assertThatThrownBy(() -> client.getSummarizedNews(fakeKeyword))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }

    @Test
    @DisplayName("주제_검증_요청_시_OpenAPI_서버_에러")
    void OPENAI_SERVER_ERROR_WHEN_REQUEST_KEYWORD_VALIDATE() {

        // given
        String fakeKeyword = "서버에러";
        given(mockRestTemplate.postForObject(
                eq("responseUrl"),
                any(),
                eq(NewsResult.class)))
                .willThrow(ExternalApiException.class);

        // when
        assertThatThrownBy(() -> client.getSummarizedNews(fakeKeyword))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("요청에 실패했습니다.");
    }

    @Test
    @DisplayName("주제_검증_요청_시_OpenAPI_타임아웃")
    void OPENAI_TIMEOUT_WHEN_REQUEST_KEYWORD_VALIDATE() {

        // given
        String fakeKeyword = "타임아웃";
        given(mockRestTemplate.postForObject(
                eq("responseUrl"),
                any(),
                eq(NewsResult.class)))
                .willThrow(ExternalApiTimeoutException.class);

        // when
        assertThatThrownBy(() -> client.getSummarizedNews(fakeKeyword))
                .isInstanceOf(ExternalApiTimeoutException.class)
                .hasMessage("연결에 실패했습니다.");
    }
}
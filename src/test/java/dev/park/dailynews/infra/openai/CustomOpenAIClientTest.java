package dev.park.dailynews.infra.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.park.dailynews.domain.news.NewsParse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@WireMockTest(httpPort = 8090)
@ExtendWith(MockitoExtension.class)
class CustomOpenAIClientTest {

    static OpenAIProperties openAIProperties() {
        return new OpenAIProperties("test-openai-key", "http://localhost:8090/v1/responses");
    }

    private CustomOpenAIClient openAIClient;

    @InjectMocks
    private ObjectMapper objectMapper;

    private RestTemplate rt;

    @BeforeEach
    void setUp() {
        rt = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofSeconds(1))
                .setConnectTimeout(Duration.ofSeconds(1))
                .build();

        openAIClient = new CustomOpenAIClient(openAIProperties(), objectMapper, rt);
    }

    @Test
    @DisplayName("뉴스_요약_OPENAI_API_요청_성공")
    void GENERATE_NEWS_SUMMARY_WITH_OPENAI() throws IOException {

        // given
        InputStream newsStream = getClass().getClassLoader().getResourceAsStream("json/openaiResponse.json");
        String newsResponse = IOUtils.toString(newsStream, StandardCharsets.UTF_8);

        stubFor(post(urlEqualTo("/v1/responses"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withBody(newsResponse)
                ));

        String keyword = "LCK";

        // when
        NewsParse newsParse = openAIClient.post(keyword);
        List<NewsParse.Items> news = newsParse.news();

        // then
        assertEquals(keyword, newsParse.topic());
        assertEquals("T1, 2025 LCK 서머 개막전서 DRX에 2-0 승리", news.get(0).headline());
        assertEquals("2025년 5월 25일, T1이 2025 LCK 서머 스플릿 개막전에서 DRX를 2대 0으로 완파했다. Faker(이상혁)는 경기 후 인터뷰에서 \"팀원들과의 호흡이 좋아졌고, 시즌 초반이지만 좋은 출발을 하게 되어 기쁘다\"고 밝혔다. LCK 사무국은 이번 시즌부터 경기 일정과 운영 방침에 일부 변화를 도입했다고 공식 발표했다.",news.get(0).summary());
        assertEquals("OSEN", newsParse.news().get(0).source());
        assertEquals("https://osen.mt.co.kr/article/20250525123456", news.get(0).url());

    }
}
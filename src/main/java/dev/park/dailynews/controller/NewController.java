package dev.park.dailynews.controller;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import dev.park.dailynews.domain.news.Subject;
import dev.park.dailynews.dto.request.AnthropicRequest;
import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.response.ApiResponse;
import dev.park.dailynews.infra.anthropic.MessageProvider;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class NewController {

    private final AnthropicClient client;
    private final MessageProvider messageProvider;

    @PostMapping("/anthropic")
    public Object sendRequest(@RequestBody AnthropicRequest anthropicRequest) {

        MessageCreateParams params = messageProvider.createParams(anthropicRequest.getMessage());
        return client.messages().create(params);
    }
}

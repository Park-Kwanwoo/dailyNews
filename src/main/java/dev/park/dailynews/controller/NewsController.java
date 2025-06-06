package dev.park.dailynews.controller;

import dev.park.dailynews.dto.request.PagingRequest;
import dev.park.dailynews.dto.response.common.ApiResponse;
import dev.park.dailynews.dto.response.common.PagingResponse;
import dev.park.dailynews.dto.response.news.NewsDetailResponse;
import dev.park.dailynews.dto.response.news.NewsResponse;
import dev.park.dailynews.model.LoginUserContext;
import dev.park.dailynews.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news")
    @Transactional(readOnly = true)
    public ApiResponse<PagingResponse<NewsResponse>> getNews(@ModelAttribute PagingRequest pagingRequest, LoginUserContext loginUserContext) {

        PagingResponse<NewsResponse> newsResponse = newsService.getNews(pagingRequest, loginUserContext.getEmail());
        return ApiResponse.successWithContent(newsResponse);
    }

    @GetMapping("/news/{newsId}")
    public ApiResponse<NewsDetailResponse> getNewsItems(LoginUserContext loginUserContext, @PathVariable Long newsId) {

        NewsDetailResponse newsItems = newsService.getNewsItems(loginUserContext.getEmail(), newsId);
        return ApiResponse.successWithContent(newsItems);
    }
}

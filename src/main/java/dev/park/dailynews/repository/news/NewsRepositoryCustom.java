package dev.park.dailynews.repository.news;

import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.dto.request.PagingRequest;
import org.springframework.data.domain.PageImpl;

public interface NewsRepositoryCustom {

    PageImpl<News> getPagingNewsList(PagingRequest pagingRequest, Long userId);

    News findWithItemsBy(Long id);
}

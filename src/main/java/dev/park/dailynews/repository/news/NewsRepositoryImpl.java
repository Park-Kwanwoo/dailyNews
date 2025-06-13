package dev.park.dailynews.repository.news;

import com.querydsl.jpa.JPQLQueryFactory;
import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.dto.request.PagingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static dev.park.dailynews.domain.news.QNews.news;
import static dev.park.dailynews.domain.news.QNewsItem.newsItem;

@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepositoryCustom {

    private final JPQLQueryFactory jpqlQueryFactory;

    @Override
    public PageImpl<News> getPagingNewsList(PagingRequest pagingRequest, Long userId) {

        Long totalCount = jpqlQueryFactory.select(news.count())
                .from(news)
                .where(news.user.id.eq(userId))
                .fetchFirst();

        List<News> items = jpqlQueryFactory.select(news)
                .from(news)
                .limit(pagingRequest.getSize())
                .offset(pagingRequest.getOffset())
                .where(news.user.id.eq(userId))
                .orderBy(news.id.desc())
                .fetch();

        return new PageImpl<>(items, pagingRequest.getPageable(), totalCount);
    }

    @Override
    public News findNewsWithItemsByNewsIdAndUserId(Long newsId, Long userId) {

        return jpqlQueryFactory.select(news)
                .from(news)
                .innerJoin(news.newsItems, newsItem)
                .fetchJoin()
                .where(news.id.eq(newsId).and(news.user.id.eq(userId)))
                .fetchOne();
    }


}

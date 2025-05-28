package dev.park.dailynews.repository.news;

import com.querydsl.jpa.JPQLQueryFactory;
import dev.park.dailynews.domain.news.News;
import dev.park.dailynews.domain.news.QNewsItem;
import dev.park.dailynews.dto.response.news.NewsResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static dev.park.dailynews.domain.news.QNews.news;
import static dev.park.dailynews.domain.news.QNewsItem.newsItem;

@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepositoryCustom {

    private final JPQLQueryFactory jpqlQueryFactory;

    @Override
    public List<NewsResponse> findNewsByUserId(Long userId) {

        return jpqlQueryFactory.select(constructor(NewsResponse.class, news.id, news.title))
                .from(news)
                .where(news.user.id.eq(userId))
                .orderBy(news.id.desc())
                .fetch();
    }

    @Override
    public News findWithItemsBy(Long id) {

        return jpqlQueryFactory.select(news)
                .from(news)
                .innerJoin(news.newsItems, newsItem)
                .where(news.id.eq(id))
                .fetchFirst();
    }


}

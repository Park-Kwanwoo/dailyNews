package dev.park.dailynews.repository.news;

import dev.park.dailynews.domain.news.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long>, NewsRepositoryCustom {

}

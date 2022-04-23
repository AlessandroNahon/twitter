package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Sentiment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SentimentRepository extends PagingAndSortingRepository<Sentiment, Long> {
}

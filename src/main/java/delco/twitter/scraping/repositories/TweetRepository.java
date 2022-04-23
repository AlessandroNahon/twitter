package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Tweet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TweetRepository extends PagingAndSortingRepository<Tweet, Long> {
}

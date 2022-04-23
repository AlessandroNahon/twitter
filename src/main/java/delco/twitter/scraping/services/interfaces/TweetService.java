package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import org.springframework.data.domain.Pageable;

public interface TweetService {

    Tweet saveTweet(Tweet tweet);

    Iterable<Tweet> saveTweets(Iterable<Tweet> tweets);

    Iterable<Tweet> findAll();

    Tweet findById(Long id);

    void delete(Tweet tweet);

    void deleteTweets();

    void deleteTweets(Iterable<Tweet> tweets);

    Tweet deleteById(Long id);

    Iterable<Tweet> findPaginated(Pageable pageable);



}

package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;

public interface RepliesService {

    Reply saveTweet(Reply tweet);

    Iterable<Reply> saveTweets(Iterable<Reply> tweets);

    Iterable<Reply> findAll();

    Reply findById(Long id);

    void delete(Reply tweet);

    void deleteTweets();

    void deleteTweets(Iterable<Reply> tweets);

    Reply deleteById(Long id);

    Iterable<Reply> findByTweetId(Long tweetId);

}

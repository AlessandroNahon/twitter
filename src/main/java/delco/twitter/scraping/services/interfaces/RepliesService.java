package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Root;

public interface RepliesService {

    Iterable<Reply> findByTweetId(Long tweetId);

    void parseReplyFromTweet(Root datum, Tweet originalTweet);


}

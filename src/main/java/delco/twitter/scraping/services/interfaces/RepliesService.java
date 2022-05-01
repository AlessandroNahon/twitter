package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Root;

import java.util.List;

public interface RepliesService {

    Iterable<Reply> findByTweetId(Long tweetId);

    void parseReplyFromTweet(Root datum, Tweet originalTweet);

    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    List<Reply> findAllByTextContaining(String text);

}

package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Root;

import java.util.Date;

public interface APITWService {

    Root getTweets(String userName, Date limitDate);

    Root getNextTweets(String username, Root lastSearch);

    Root getReplies(String conversationId, Tweet originalTweet);



    String getUserId(String username);

}

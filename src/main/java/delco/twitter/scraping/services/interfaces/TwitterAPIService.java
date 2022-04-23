package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Root;

import java.util.Date;

public interface TwitterAPIService {

    void getTweets(String userName, Date limitDate);

    void getNextTweets(String username, Root lastSearch);

    void getReplies(String conversationId, Tweet originalTweet);

    boolean isRetweet(String tweet);

    boolean containsMedia(Datum datum);

    void setTweetImage(Includes include, Datum datum, Tweet originalTweet);

    void setReplyImage(Includes include, Datum datum, Reply originalTweet);

    String getUserId(String username);

    void parseTweetDatumFromRoot(Root root, String username);

    void parseReplyDatumFromRoot(Root root, Tweet originalTweet);

}

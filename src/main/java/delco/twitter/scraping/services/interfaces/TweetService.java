package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Datum;
import delco.twitter.scraping.model.twitterapi.model_content.Root;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface TweetService {

    void parseTweetDatumFromRoot(Root root, String username);

    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    List<Tweet> findByText(String text);

    List<Tweet> findAllTweets();

    Set<String> getAllEmojisFromTweets(Tweet t);

    /*
    METHODS TO FIND ALL THE POSITIVE CONTENT IN THE DATABASE
     */

    List<Tweet> getTweetsPositiveTextAndPositiveImage();

    Integer getCountTweetsPositiveTextAndPositiveImage();

    List<Tweet> getTweetsPositiveTextAndPositiveEmojis();

    Integer getCountTweetsPositiveTextAndPositiveEmojis();

    List<Tweet> getFullMatchesTweets();

    Integer getCountFullMatchesTweets();


    /*
    METHODS TO FIND ALL THE NEGATIVE CONTENT IN THE DATABASE
     */

    List<Tweet> getTweetsNegativeTextAndNegativeImage();

    Integer getCountTweetsNegativeTextAndNegativeImage();

    List<Tweet> getTweetsNegativeTextAndNegativeEmojis();

    Integer getCountTweetsNegativeTextAndNegativeEmojis();

    List<Tweet> getFullNegativeMatchesTweets();

    Integer getCountFullNegativeMatchesTweets();



}

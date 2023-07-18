package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
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

    void deleteAllInfo(Long id);

    void changeSentiment(String sentiment, Long id);


    List<Tweet> findByText(String text, String username);


    List<Tweet> findAllTweets();

    Set<String> getAllEmojisFromTweets(Tweet t);

    List<Tweet> findBySentiment(String sentiment);

    List<Tweet> findByUsername(String username);

    List<Tweet> findByImageContent(String imageContent);

    /*
    Methods to analyze the sentiment of the tweets
     */

    List<Tweet> findTextImage(String username, boolean wantPositive);

    List<Tweet> findText(String username, boolean wantPositive);

    List<Tweet> findTextEmoji(String username, boolean wantPositive);

    List<Tweet> findFullMatches(String username, boolean wantPositive);

    List<Tweet> findAllOthers(String username);

    List<Tweet> getBySentiment(String username, boolean wantPositive);

    List<Tweet> getCountBySentiment(String username,boolean wantPositive);





}

package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Datum;
import delco.twitter.scraping.model.twitterapi.model_content.Root;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface TweetService {

    void parseTweetDatumFromRoot(Root root, String username);

    Set<String> getAllEmojisFromTweets(Tweet t);

    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    List<Tweet> findByText(String text);

    List<Tweet> findTop5ByOrderByIdDesc();

    List<Integer> analyzeDatabase(String typeOfSearch);



}

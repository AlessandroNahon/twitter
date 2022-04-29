package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Root;

import java.util.Date;
import java.util.List;


public interface TweetService {

    void parseTweetDatumFromRoot(Root root, String username);

    void getUserTimeline(String username, Date maxDate);

    boolean isRetweet(String tweet);

    boolean containsMedia(Datum datum);

    List<Tweet> findByText(String text);

    List<Tweet> findTop5ByOrderByIdDesc();



}

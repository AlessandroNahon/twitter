package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;

import java.util.Date;

public interface TwitterAPIService {

    Root getTweets(String userName,String startDate, String endDate);

    Root getNextTweets(String paginationToken, String startDate, String endDate);

    Root getReplies(String conversationId);


}

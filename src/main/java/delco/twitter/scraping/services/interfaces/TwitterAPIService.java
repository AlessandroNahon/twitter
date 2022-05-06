package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.twitterapi.model_content.Root;

public interface TwitterAPIService {

    Root getTweets(String userName,String startDate, String endDate);

    Root getNextTweets(String paginationToken, String startDate, String endDate);

    Root getReplies(String conversationId, String sinceId);


}

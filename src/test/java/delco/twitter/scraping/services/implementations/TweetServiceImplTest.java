package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TweetServiceImplTest {

    private TwitterAPIService twitterAPIService;
    private TweetService tweetService;
    String brearerToken = "bearerToken";
    String endDate = "2022-05-01"+"T00:00:00-00:00";
    String startDate = "2022-01-01"+"T00:00:00-00:00";


    @BeforeEach
    void setup(){
        twitterAPIService = new TwitterAPIServiceImpl();
        tweetService = new TweetServiceImpl();
    }

    @Test
    void parseTweetDatumFromRoot() {
        Root r = twitterAPIService.getTweets("Greenpeace",startDate,endDate);
        tweetService.parseTweetDatumFromRoot(r, "Greenpeace");
    }
}
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
    String brearerToken = "AAAAAAAAAAAAAAAAAAAAAFelbAEAAAAA6BKWPBKmWTviEy2Pr1BPj1yhh3Q%3D8Kq8C7d0Vl9vOE0LwXroMtAiYriC5yq9FperLTjQBCNIXXndam";
    String endDate = "2022-05-01"+"T00:00:00-00:00";
    String startDate = "2022-01-01"+"T00:00:00-00:00";


    @BeforeEach
    void setup(){
        twitterAPIService = new TwitterAPIServiceImpl(brearerToken);
        tweetService = new TweetServiceImpl();
    }

    @Test
    void parseTweetDatumFromRoot() {
        Root r = twitterAPIService.getTweets("Greenpeace",startDate,endDate);
        tweetService.parseTweetDatumFromRoot(r, "Greenpeace");
    }
}
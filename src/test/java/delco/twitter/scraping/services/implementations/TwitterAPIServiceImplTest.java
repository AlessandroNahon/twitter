package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(Parameterized.class)
class TwitterAPIServiceImplTest {

    public static TwitterAPIService twitterAPIService;
    public static String beare = "AAAAAAAAAAAAAAAAAAAAAFelbAEAAAAA6BKWPBKmWTviEy2Pr1BPj1yhh3Q%3D8Kq8C7d0Vl9vOE0LwXroMtAiYriC5yq9FperLTjQBCNIXXndam";
    public static String endDate = "2022-05-01"+"T00:00:00-00:00";
    public static String startDate = "2022-01-01"+"T00:00:00-00:00";

    @BeforeEach
    void setup(){
        twitterAPIService = new TwitterAPIServiceImpl(beare);
    }

    @Test
    void checkGetTweets() {
        Root r = twitterAPIService.getTweets("Greenpeace",startDate,endDate);
        System.out.println(r.toString());
        assertNotNull(r);
    }

    @Test
    void checkGetNextTweets(){
        Root r = twitterAPIService.getTweets("Greenpeace",startDate,endDate);
        System.out.println(r.toString());
        Root r2 = twitterAPIService.
                getNextTweets(r.getMeta().getNext_token(),startDate,endDate);
        System.out.println(r2.toString());
        assertFalse(r2.getData().isEmpty());
    }

    public static List<String> getConversationsId(){
        twitterAPIService = new TwitterAPIServiceImpl(beare);
        List<String> listOfConversationId = new ArrayList<>();
        Root r = twitterAPIService.getTweets("Greenpeace",startDate,endDate);
        System.out.println(r.toString());
        r.getData().forEach(e -> listOfConversationId.add(e.getConversation_id()));
        listOfConversationId.forEach(System.out::println);
        return listOfConversationId;
    }

    @ParameterizedTest
    @MethodSource("getConversationsId")
    void getRepliesFromConversationsId(String arg){
        Root r = twitterAPIService.getReplies(arg);
        System.out.println(r.toString());
        assertFalse(r.getData().isEmpty());
    }
}
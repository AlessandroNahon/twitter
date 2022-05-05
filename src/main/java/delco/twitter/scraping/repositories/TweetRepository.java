package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;


public interface TweetRepository  extends PagingAndSortingRepository<Tweet, Long>, JpaRepository<Tweet, Long> {

    Tweet findTop1ByOrderByIdDesc();

    List<Tweet> findAllByTextContaining(String text);

    List<Tweet> findTop5ByOrderByIdDesc();

    @Query(value = "SELECT * FROM twitter.tweets t where t.id in (select tweet_id from image i where i.image_content" +
            " = 'GROTESQUE') and t.text_sentiment =  'NEGATIVE' OR t.text_sentiment = 'VERY_NEGATIVE'", nativeQuery = true)
    List<Tweet> findAllFullNegativeTweets();

    @Query(value = "SELECT * FROM twitter.tweets t where t.id in (select tweet_id from image i where i.image_content" +
            " = 'KITSCH') and t.text_sentiment =  'POSITIVE' OR t.text_sentiment = 'VERY_POSITIVE'", nativeQuery = true)
    List<Tweet> findAllFullPositiveTweets();

    @Query(value = "select * from twitter.tweets t where t.id not in (SELECT t.id FROM twitter.tweets t where t.id in \n" +
            "(select tweet_id from image i where i.image_content = 'GROTESQUE') and t.text_sentiment = 'NEGATIVE' \n" +
            "OR t.text_sentiment = 'VERY_NEGATIVE')and t.id not in (SELECT t.id FROM twitter.tweets t where t.id in \n" +
            "(select tweet_id from image i where i.image_content = 'KITSCH') and t.text_sentiment = 'POSITIVE' \n" +
            "OR t.text_sentiment = 'VERY_POSITIVE')", nativeQuery = true)
    List<Tweet> findAllOtherTweets();




}

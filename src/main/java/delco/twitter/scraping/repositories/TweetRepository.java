package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
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

    List<Tweet> findAllByTextSentiment(SentimentEnum sentiment);

    List<Tweet> findByUsername(String username);

    @Query(value = "select * from twitter.tweets t where t.id in " +
            "(select tweet_id from image i where i.image_content = ?1)", nativeQuery = true)
    List<Tweet> findByImageContent(String imageContent);


     // =============================================
     //           FIND POSITIVE CONTENT
     // =============================================

    /**
     * Find all tweets with positive sentiment and with an kistch image into the database
     * @return List of tweets with positive sentiment and with an kistch image
     */
    @Query(value = "SELECT * FROM twitter.tweets t where t.id in (select tweet_id from image i where i.image_content" +
            " = 'KITSCH') and t.text_sentiment =  'POSITIVE' OR t.text_sentiment = 'VERY_POSITIVE'", nativeQuery = true)
    List<Tweet> getTextImagePositive();

    /**
     * This method is used to find in the database all those tweets that has positive or very positive text
     * @return List of positive tweets
     */
    @Query(value = "Select * from twitter.tweets t where t.text_sentiment = " +
            "'POSITIVE' OR t.text_sentiment = 'VERY_POSITIVE'", nativeQuery = true)
    List<Tweet> getTextPositive();


    // =============================================
    //           FIND NEGATIVE CONTENT
    // =============================================

    /**
     * This method is used to get the count of Tweets that are negative and has attached images with also
     * negative sentiment
     */
    @Query(value = "SELECT * FROM twitter.tweets t where t.id in (select tweet_id from image i where i.image_content" +
            " = 'GROTESQUE') and t.text_sentiment =  'NEGATIVE' OR t.text_sentiment = 'VERY_NEGATIVE'", nativeQuery = true)
    List<Tweet> getTextImageNegative();

    /**
     * This method is used to find in the database all those tweets that has positive or very positive text
     * @return List of positive tweets
     */
    @Query(value = "Select * from twitter.tweets t where t.text_sentiment = " +
            "'NEGATIVE' OR t.text_sentiment = 'VERY_NEGATIVE'", nativeQuery = true)
    List<Tweet> getTextNegative();


    // =============================================
    //           FIND GRAY CONTENT
    // =============================================





}

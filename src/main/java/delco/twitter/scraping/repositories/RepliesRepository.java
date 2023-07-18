package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;
import java.util.List;

public interface RepliesRepository extends PagingAndSortingRepository<Reply, Long>, JpaRepository<Reply, Long> {

    List<Reply> findAllByTextContaining(String text);

    @Modifying
    @Query(value = "delete from responses r where r.original_tweet_id = ?1", nativeQuery = true)
    void deleteAllByOriginalTweet(Long id);


    List<Reply> findByOrganization(String organization);

    List<Reply> findByOrganizationAndTextSentiment(String organization, SentimentEnum sentiment);

    @Query(value = "select * from twitter.responses r where r.text like %?1% and r.organization = ?2", nativeQuery = true)
    List<Reply> findAllByTextContaining(String text, String organization);

    // =============================================
    //           FIND POSITIVE CONTENT
    // =============================================

    /**
     * Find all tweets with positive sentiment and with an kistch image into the database
     * @return List of tweets with positive sentiment and with an kistch image
     */
    @Query(value = "SELECT * FROM twitter.responses r where r.id in (select reply_id from image i where i.image_content" +
            " = 'KITSCH') and r.organization = ?1 and (r.text_sentiment = 'POSITIVE' OR r.text_sentiment = 'VERY_POSITIVE')", nativeQuery = true)
    List<Reply> getTextImagePositive(String organization);

    /**
     * This method is used to find in the database all those tweets that has negative or very negative text
     * @return List of negative replies
     */
    @Query(value = "Select * from twitter.responses r where (r.text_sentiment = " +
            "'POSITIVE' OR r.text_sentiment = 'VERY_POSITIVE') and r.organization = ?1", nativeQuery = true)
    List<Reply> getTextPositive(String organization);




    // =============================================
    //           FIND NEGATIVE CONTENT
    // =============================================

    @Query(value = "SELECT * FROM twitter.responses r where r.id in (select reply_id from image i where i.image_content" +
            " = 'GROTESQUE') and r.organization = ?1 and (r.text_sentiment =  'NEGATIVE' OR r.text_sentiment = 'VERY_NEGATIVE')", nativeQuery = true)
    List<Reply> getTextImageNegative(String organization);

    /**
     * This method is used to find in the database all those tweets that has negative or very negative text
     * @return List of negative replies
     */
    @Query(value = "Select * from twitter.responses r where (r.text_sentiment = " +
            "'NEGATIVE' OR r.text_sentiment = 'VERY_NEGATIVE') and r.organization = ?1", nativeQuery = true)
    List<Reply> getTextNegative(String organization);




}

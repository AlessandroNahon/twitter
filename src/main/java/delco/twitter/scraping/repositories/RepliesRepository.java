package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RepliesRepository extends PagingAndSortingRepository<Reply, Long>, JpaRepository<Reply, Long> {

    List<Reply> findAllByTextContaining(String text);

    @Query(value = "SELECT * FROM twitter.responses t where t.id in (select reply_id from image i where i.image_content" +
            " = 'GROTESQUE') and t.text_sentiment =  'NEGATIVE' OR t.text_sentiment = 'VERY_NEGATIVE'", nativeQuery = true)
    List<Tweet> findAllFullNegativeTweets();

    @Query(value = "SELECT * FROM twitter.responses t where t.id in (select reply_id from image i where i.image_content" +
            " = 'KITSCH') and t.text_sentiment =  'POSITIVE' OR t.text_sentiment = 'VERY_POSITIVE'", nativeQuery = true)
    List<Tweet> findAllFullPositiveTweets();

    @Query(value = "select * from twitter.responses t where t.id not in (SELECT t.id FROM twitter.responses t where t.id in \n" +
            "(select reply_id from image i where i.image_content = 'GROTESQUE') and t.text_sentiment = 'NEGATIVE' \n" +
            "OR t.text_sentiment = 'VERY_NEGATIVE')and t.id not in (SELECT t.id FROM twitter.tweets t where t.id in \n" +
            "(select reply_id from image i where i.image_content = 'KITSCH') and t.text_sentiment = 'POSITIVE' \n" +
            "OR t.text_sentiment = 'VERY_POSITIVE')", nativeQuery = true)
    List<Tweet> findAllOtherReplies();

}

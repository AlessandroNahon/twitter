package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Images;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ImageRepository extends PagingAndSortingRepository<Images, Long> {


    List<Images> findByReplyIsNull();

    List<Images> findByReplyId(Long id);

    @Modifying
    @Query(value = "delete from image i where i.tweet_id = ?1", nativeQuery = true)
    void deleteByTweetId(Long id);

    @Modifying
    @Query(value = "delete from image i where i.reply_id = ?1", nativeQuery = true)
    void deleteByReplyId(Long id);


}

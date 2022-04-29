package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
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




}

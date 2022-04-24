package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

import javax.persistence.*;
import java.sql.Date;


public interface TweetRepository  extends PagingAndSortingRepository<Tweet, Long>, JpaRepository<Tweet, Long> {

}

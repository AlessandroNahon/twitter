package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RepliesRepository extends PagingAndSortingRepository<Reply, Long>, JpaRepository<Reply, Long> {



}

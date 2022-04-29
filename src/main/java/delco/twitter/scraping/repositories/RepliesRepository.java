package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RepliesRepository extends PagingAndSortingRepository<Reply, Long>, JpaRepository<Reply, Long> {

    List<Reply> findAllByTextContaining(String text);

}

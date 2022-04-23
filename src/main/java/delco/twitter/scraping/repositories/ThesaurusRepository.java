package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Word;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ThesaurusRepository extends PagingAndSortingRepository<Word, Long> {
}

package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Sentiments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SentimentRepository extends PagingAndSortingRepository<Sentiments, Long> {

    @Query(value = "SELECT * FROM twitter.sentiments s WHERE s.belongs_to = ?1 and s.organization = ?2 and sentiment = ?3", nativeQuery = true)
    Sentiments findSpecificSentiment(String belongs_to, String organization, String sentiment);

    @Query(value = "SELECT * FROM twitter.sentiments s WHERE s.belongs_to = ?1 and s.organization = ?2", nativeQuery = true)
    List<Sentiments> findAllSentimentsByOrganizationAndBelongsTo(String belongs_to, String organization);

}

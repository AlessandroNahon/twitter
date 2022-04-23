package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {
}

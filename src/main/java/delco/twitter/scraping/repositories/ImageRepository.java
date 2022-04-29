package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Images;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ImageRepository extends PagingAndSortingRepository<Images, Long> {


}

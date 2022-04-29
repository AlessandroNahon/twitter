package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface WordRepository extends PagingAndSortingRepository<Word, Long>, JpaRepository<Word, Long> {


    Word findTop1BySyntaxOrderByCountDesc (TypeEnum syntax);

    List<Word> findTop20ByOrderByCountDesc();

    List<Word> findTop5ByOrderByCountDesc();

    List<Word> findTop10BySyntaxOrderByCountDesc (TypeEnum syntax);






}

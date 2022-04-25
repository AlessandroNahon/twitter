package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SyntaxEnum;
import jdk.nashorn.internal.runtime.regexp.joni.Syntax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends PagingAndSortingRepository<Word, Long>, JpaRepository<Word, Long> {


    Word findTop1BySyntaxOrderByCountDesc (SyntaxEnum syntax);

    List<Word> findTop20ByOrderByCountDesc();

    List<Word> findTop5ByOrderByCountDesc();

    List<Word> findTop10BySyntaxOrderByCountDesc (SyntaxEnum syntax);






}

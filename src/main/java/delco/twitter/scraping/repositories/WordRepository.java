package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface WordRepository extends PagingAndSortingRepository<Word, Long>, JpaRepository<Word, Long> {


    void deleteByWord(String text);

    Word findByWordAndBelongsTo(String word, String belongs_to);

    List<Word> findTop20ByBelongsToAndSyntaxOrderByCountDesc (String belongs_to, TypeEnum syntax);

    List<Word> findTop5ByOrderByCountDesc();

    List<Word> findTop20ByBelongsToOrderByCountDesc (String belongs_to);

    @Query(value = "SELECT * from twitter.words w where w.syntax like '%EMOJI%' " +
            "and w.belongs_to = ?1 order by w.count desc limit 1", nativeQuery = true)
    Word findTopEmojiByBelongsTo(String belongs_to);



    @Query(value = "select * from twitter.words w where w.belongs_to = ?1 and w.syntax like ?2", nativeQuery = true)
    List<Word> findAllByBelongsToAndSyntax(String belongs_to, String syntax);





}

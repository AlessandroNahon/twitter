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

    List<Word> findAllByBelongsTo(String belongs_to);

    List<Word> findTop20ByBelongsToAndSyntaxOrderByCountDesc (String belongs_to, TypeEnum syntax);

    List<Word> findTop10ByBelongsToAndSyntaxOrderByCountDesc (String belongs_to, TypeEnum syntax);

    List<Word> findTop5ByOrderByCountDesc();

    Word findTop1ByBelongsToAndSyntaxOrderByCountDesc (String belongs_to, TypeEnum syntax);

    List<Word> findTop20ByBelongsToOrderByCountDesc (String belongs_to);

    @Query(value = "SELECT * from twitter.words w where w.syntax like '%EMOJI%' " +
            "and w.belongs_to = ?1 order by w.count desc limit 1", nativeQuery = true)
    Word findTopEmojiByBelongsTo(String belongs_to);

    /**
     * This method is used to get all the emojis that belongs to a Tweet or a Reply. This method is used in order
     * to archive a collection of emojis so we can define if the Tweet/Reply can be considered a full postive/negative
     * (See sentiment page)
     * @param belongs_to
     * @return
     */
    @Query(value =  "Select * from twitter.words w where w.syntax like '%EMOJI%' " +
            "and w.belongs_to = ?1", nativeQuery = true)
    List<Word> findAllEmojiByBelongsTo(String belongs_to);


    @Query(value = "select * from twitter.words w where w.belongs_to = ?1 and w.syntax like '% ?2 %'", nativeQuery = true)
    List<Word> findAllByBelongsToAndSyntax(String belongs_to, String syntax);





}

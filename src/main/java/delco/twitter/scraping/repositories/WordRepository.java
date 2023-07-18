package delco.twitter.scraping.repositories;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface WordRepository extends PagingAndSortingRepository<Word, Long>, JpaRepository<Word, Long> {


    void deleteByWord(String text);

    Word findByWordAndBelongsToAndOrganization(String word, String belongs_to, String organization);

    @Query(value = "select * from twitter.words w where w.belongs_to = ?1 and w.syntax like ?2 and w.organization = ?3", nativeQuery = true)
    List<Word> findAllByBelongsSyntaxOrganization(String belongs_to, String syntax, String organization);

    @Query(value = "select * from twitter.words w where w.belongs_to = ?1 and w.syntax like ?2 and w.organization = ?3 order by w.count DESC", nativeQuery = true)
    List<Word> findSortedByBelongSyntaxOrganization(String belongs_to, String syntax, String organization);

    @Query(value = "select * from twitter.words w where w.belongs_to = ?1 and w.organization like ?2 order by w.count DESC", nativeQuery = true)
    List<Word> findSortedByBelongsAndOrganization(String belongs_to, String organization);

    @Query(value = "select * from twitter.words w where w.belongs_to = ?1  and w.syntax not like '%EMOJI%' and w.organization like ?2 order by w.count DESC", nativeQuery = true)
    List<Word> findSortedByBelongsAndOrganizationNoEmoji(String belongs_to, String organization);

    @Query(value = "select * from twitter.words w where w.belongs_to = ?1 and w.syntax = ?2 and w.organization = ?3 order by w.count DESC limit 1", nativeQuery = true)
    Word findTopByBelongsSyntaxAndOrganization(String belongs_to, String syntax, String organization);




}

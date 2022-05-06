package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import java.lang.reflect.Type;
import java.util.List;

public interface WordService {



    void analyzeText(String text, String belongs_to);

    TypeEnum getTypeOfWord(String word);

    boolean isKischWord(String words);

    boolean isGrotesqueWord(String word);

    boolean isGrotesqueEmoji(String word);

    int getEmojiEndPos(char[] text, int startPos);

    void parseWord(String text, TypeEnum syntax, String belongs_to);

    List<Word> getAllWordsFromTweet(Tweet t);

    List<Word> getCountOfWordsFromText(String text);

    List<Word> sortByCountFilterBySyntax(List<Word> listOfWords, TypeEnum ... target);




    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    List<String> getAllEmojisFromText(String text);

    List<Word> getAllWordsByBelongTo(String belong_to);

    List<Word> getTop20ByBelongsTo(String belongs_to);

    List<Word> getTop5Words();

    List<Word> getTop20WordsByBelongsToBySyntax (String belongs_to, TypeEnum syntax);

    List<Word> getTop10ByBelongsToBySyntax (String belongs_to, TypeEnum syntax);

    Word getTopByBelongsToBySyntax (String belongs_to, TypeEnum syntax);

    Word getByWordAndBelongsTo(String word, String belongs_to);

    Word getTopEmojiByBelongsTo(String belongs_to);







}

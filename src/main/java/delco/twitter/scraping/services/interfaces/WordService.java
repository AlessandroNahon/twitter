package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SyntaxEnum;

import java.util.List;
import java.util.Map;

public interface WordService {



    void analyzeText(String text);

    SyntaxEnum getTypeOfWord(String word);

    Word isWordPresent(String word);

    boolean isEmoji(String word);

    void parseWord(String text);

    void parseEmoji(String emoji);



    void parseKischWord(String text);

    void parseGrotesqueWord(String text);

    boolean isKischWord(String words);

    boolean isGrotesqueWord(String word);

    List<Word> getTop20Words();

    List<Word> getTop5Words();



}

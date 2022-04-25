package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SyntaxEnum;

import java.util.Map;

public interface WordService {



    void analyzeText(String text);

    SyntaxEnum getTypeOfWord(String word);

    Object[] getWordAndCount(int numberOfWords);

    Word isWordPresent(String word);

    boolean isEmoji(String word);

    void parseWord(String text);

    void parseEmoji(String emoji);



    void parseKischWord(String text);

    void parseGrotesqueWord(String text);

    boolean isKischWord(String words);

    boolean isGrotesqueWord(String word);



}

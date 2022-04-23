package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SyntaxEnum;

import java.util.Map;

public interface WordService {



    void analyzeText(String text);

    Map<String, Integer> sortWords();

    SyntaxEnum getTypeOfWord(String word);

    Object[] getWordAndCount(int numberOfWords);

    Word isWordPresent(String word);

}

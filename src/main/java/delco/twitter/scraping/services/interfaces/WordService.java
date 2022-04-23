package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Word;

import java.util.Map;

public interface WordService {



    void analyzeText(String text);

    Map<String, Integer> sortWords();

    boolean isImportantWord(String word);

    Object[] getFiveWords();

    Word isWordPresent(String word);

}

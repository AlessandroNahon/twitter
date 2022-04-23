package delco.twitter.scraping.services.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ThesaurusService {



    void addText(String text);

    Map<String, Integer> sortWords();

    boolean isImportantWord(String word);

    Object[] getFiveWords();

}

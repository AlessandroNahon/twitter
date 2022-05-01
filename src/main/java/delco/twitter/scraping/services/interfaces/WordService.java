package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;

import java.util.List;

public interface WordService {


    void analyzeText(String text);

    TypeEnum getTypeOfWord(String word);

    Word isWordPresent(String word);

    boolean isKischWord(String words);

    boolean isGrotesqueWord(String word);

    int getEmojiEndPos(char[] text, int startPos);

    void parseWord(String text, TypeEnum syntax);


    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    List<Word> getTop20Words();

    List<Word> getTop5Words();

    List<String> getAllEmojisFromText(String text);




}

package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import java.lang.reflect.Type;
import java.util.List;

public interface WordService {



    void analyzeText(String text, String belongs_to, String organization);

    TypeEnum getTypeOfWord(String word);

    boolean textContainsKistch(String text);

    boolean textContainsGrotesque(String text);

    boolean isKischWord(String words);

    boolean isKistchEmoji(String emoji);

    boolean isGrotesqueWord(String word);

    boolean isGrotesqueEmoji(String word);

    int getEmojiEndPos(char[] text, int startPos);

    void parseWord(String text, TypeEnum syntax, String belongs_to, String organization);

    List<Word> getAllWordsFromTweet(Tweet t);

    List<Word> getCountOfWordsFromText(String text);

    List<Word> sortByCountFilterBySyntax(List<Word> listOfWords, TypeEnum ... target);




    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    List<String> getAllEmojisFromText(String text);

    Word getByWordAndBelongsToAndOrganization(String word, String belongs_to, String organization);

    List<Word> getByBelongsToAndOrganizationBySyntax(String belongs_to, TypeEnum syntax, String organization);

    List<Word> getSortedByBelongsAndOrganization(String belongs_to, String organization,boolean wantEmoji, int limit);

    List<Word> getSortedByBelongsOrganizationAndSyntax(String belongs_to, String organization, TypeEnum syntax, int limit);

    Word getTopByBelongsSyntaxAndOrganization(String belongs_to, TypeEnum syntax, String organization);

    Word getTopEmojiByBelongsAndOrganization(String belongs_to, String organization);








}

package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.interfaces.WordService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(Parameterized.class)
class WordServiceImplTest {


    private WordService wordService;
    private Tweet testTweet;

    @BeforeEach
    void setup(){
        wordService = new WordServiceImpl();
        Set<Reply> replies = new HashSet<>();
        replies.add(Reply.builder().text("Segundo tweet regular harmful de prueba \uD83D\uDE9A").build());
        replies.add(Reply.builder().text("Tercero \uD83D\uDE9A tweet de \uD83D\uDE9A prueba like").build());
        replies.add(Reply.builder().text("Cuarto tweet harmful de prueba devastating").build());
        testTweet= Tweet.builder()
                .text("Esta es la prueba de como debe de estar funcionando esto esto esto esto")
                .replies(replies)
                .build();
    }

    @Test
    void getAllWordsFromTweet() {
        List<Word> listaPalabras = wordService.getAllWordsFromTweet(testTweet);
        for(Word w : listaPalabras){
            System.out.println(w.toString());
        }
        assertNotNull(listaPalabras);
    }

    @ParameterizedTest
    @MethodSource("getTypeEnum")
    void sortByCountFilterBySyntax(TypeEnum typeEnum){
        List<Word> fullList = wordService.getAllWordsFromTweet(testTweet);
        List<Word> testList = wordService.sortByCountFilterBySyntax(fullList, typeEnum);
        for (Word w : testList){
            System.out.println(w.toString());
        }
        assertNotNull(testList);
    }

    public static List<TypeEnum> getTypeEnum(){
        List<TypeEnum> typeEnums = new ArrayList<>();
        typeEnums.add(TypeEnum.EMOJI);
        typeEnums.add(TypeEnum.GROTESQUE);
        typeEnums.add(TypeEnum.KITSCH);
        typeEnums.add(TypeEnum.NOUN);
        return typeEnums;
    }
}
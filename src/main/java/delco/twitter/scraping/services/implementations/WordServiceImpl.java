package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SyntaxEnum;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.interfaces.WordService;
import delco.twitter.scraping.services.pipelinenlp.Pipeline;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class WordServiceImpl implements WordService {

    private Map<String, Integer> wordList = new HashMap<>();
    private final WordRepository wordRepository;
    private StanfordCoreNLP stanfordCoreNLP;

    public WordServiceImpl(WordRepository wordRepository){
        this.wordRepository = wordRepository;
        stanfordCoreNLP = Pipeline.getPipeline();

    }


    @Override
    public void analyzeText(String text) {
        String[] words = text.split(" ");
        for (String word : words) {
            if(word.length()>3 && !word.contains("@") && !word.contains("http")) {
                word = word.replace(",", "")
                        .replace("&gt;&gt","")
                        .replace("&gt","");
                SyntaxEnum syntaxEnum = getTypeOfWord(word);
                if (syntaxEnum != SyntaxEnum.NONE) {
                    try{
                        Word newWord = isWordPresent(word);
                        if (newWord != null) {
                            newWord.setCount(newWord.getCount()+1);
                        } else {
                            newWord = new Word(word, 1, syntaxEnum);
                        }
                        wordRepository.save(newWord);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                }
            }
        }
    }



    @Override
    public SyntaxEnum getTypeOfWord(String word) {
        CoreDocument coreDocument = new CoreDocument(word);
        this.stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList = coreDocument.tokens();
        for(CoreLabel coreLabel : coreLabelList) {
            String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            switch (pos) {
                case "NN":
                    return SyntaxEnum.NOUN_SINGULAR;
                case "NNS":
                    return SyntaxEnum.NOUN_PLURAL;
                case "NNP":
                    return SyntaxEnum.PROPER_NOUN_SINGULAR;
                case "NNPS":
                    return SyntaxEnum.PROPER_NOUN_PLURAL;
                case "JJ":
                    return SyntaxEnum.ADJECTIVE;
                case "JJR":
                    return SyntaxEnum.ADJECTIVE_COMPARATIVE;
                case "JJS":
                    return SyntaxEnum.ADJECTIVE_SUPERLATIVE;
                case "RB":
                    return SyntaxEnum.ADVERB;
                case "RBR":
                    return SyntaxEnum.ADVERB_COMPARATIVE;
                case "RBS":
                    return SyntaxEnum.ADVERB_SUPERLATIVE;
                case "WRB":
                    return SyntaxEnum.WHADVERB;
                default:
                    return SyntaxEnum.NONE;
            }
        }
        return SyntaxEnum.NONE;
    }


    @Override
    public Object[] getWordAndCount(int numberOfWords) {
        List<Word> wordList = wordRepository
                .findAll(PageRequest.of(0, numberOfWords, Sort.by("count")
                .descending())).getContent();
        Object[] palabras = new Object[5];
        Object[] contador = new Object[5];
        for(int i = 0; i<5; i++) {
            palabras[i] = wordList.get(i).getWord();
            contador[i] = wordList.get(i).getCount();
        }
        return new Object[][]{palabras,contador};
    }

    @Override
    public Word isWordPresent(String word) {
        return  StreamSupport.stream(wordRepository.findAll().spliterator(),false)
                .filter(w -> w.getWord().equals(word)).findAny().orElse(null);
    }

}

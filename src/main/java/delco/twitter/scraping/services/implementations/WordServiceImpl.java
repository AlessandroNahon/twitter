package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.interfaces.WordService;
import delco.twitter.scraping.services.pipelinenlp.Pipeline;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class WordServiceImpl implements WordService {

    private Map<String, Integer> wordList = new HashMap();
    private final WordRepository wordRepository;

    public WordServiceImpl(WordRepository wordRepository){
        this.wordRepository = wordRepository;
        wordRepository.findAll().forEach(word -> wordList.put(word.getWord(),word.getCount()));
    }


    @Override
    public void analyzeText(String text) {
        String[] words = text.split(" ");
        for (String word : words) {
            if(word.length()>3 && !word.contains("@") && !word.contains("http")) {
                word = word.replace(",", "");
                if(isImportantWord(word)) {
                    if(wordList.containsKey(word.toLowerCase())) {
                        wordList.put(word.toLowerCase(),wordList.get(word.toLowerCase())+1);
                    }else{
                        wordList.put(word.toLowerCase(),1);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Integer> sortWords() {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(wordList.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


    @Override
    public boolean isImportantWord(String word) {
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        CoreDocument coreDocument = new CoreDocument(word);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList = coreDocument.tokens();
        for(CoreLabel coreLabel : coreLabelList) {
            String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if(pos.equals("WRB")||pos.equals("JJ")||pos.equals("JJR")||pos.equals("JJS")
                    ||pos.equals("NN")||pos.equals("NNS")||pos.equals("NNP")||pos.equals("NNPS")
            || pos.equals("RB")||pos.equals("RBR")||pos.equals("RBS")){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }


    @Override
    public Object[] getFiveWords() {
        Map<String, Integer> sortedMap = sortWords();
        Object[] palabras = new Object[5];
        Object[] contador = new Object[5];
        for(int i = 0; i<5; i++) {
            palabras[i] = sortedMap.keySet().toArray()[i];
            contador[i] = sortedMap.values().toArray()[i];
        }
        return new Object[][]{palabras,contador};
    }

    @Override
    public Word isWordPresent(String word) {
        return  StreamSupport.stream(wordRepository.findAll().spliterator(),false)
                .filter(w -> w.getWord().equals(word)).findAny().orElse(null);
    }

}

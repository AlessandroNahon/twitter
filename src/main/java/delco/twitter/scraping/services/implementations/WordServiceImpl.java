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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.persistence.EntityManager;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Service
public class WordServiceImpl extends Thread  implements WordService {

    private Map<String, Integer> wordList = new HashMap<>();
    private final WordRepository wordRepository;
    private StanfordCoreNLP stanfordCoreNLP;
    private final String regex = "[\\x{10000}-\\x{10FFFF}]";
    private final Set<String> kischcWords = readFiles("kische");
    private final Set<String> grotesqueWords = readFiles("grotesque");
    @Autowired
    private EntityManager em;
    private boolean loadedPipeline = false;
    private final Thread thread = this;

    public WordServiceImpl(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        new Thread(new Runnable() {
            @Override
            public void run() {
                stanfordCoreNLP = Pipeline.getPipeline();
                loadedPipeline = true;
            }
        }).start();

    }

    /**
     * This method is used to do an analysis over the text of each tweet and save the words in the database.
     * This method checks each words and if its length is longer than 3 (To avoid words like 'he', 'to', or 'yes',
     * doesn't contains the word http or @ (Which means it is a link or a mention to another username) the word will
     * be cleaned and analyzed by the StanfordCoreNLP, to determine if it is an important word. Also, this method checks
     * if the word contains emojis, calling another method to do the same as with the words.
     * See method getTypeOfWord to see the available types of words.
     * @param text The text of the tweet
     */
    @Override
    public void analyzeText(String text) {
        String[] words = text.split(" ");
        for (String word : words) {
            if (word.length() > 3 && !word.contains("@") && !word.contains("http")) {
                word = word.replace(",", "")
                        .replace("&gt;&gt", "")
                        .replace("&gt", "")
                        .replace("\n"," ")
                        .replace(".", "")
                        .replace("?", "")
                        .replace("!", "");
                if (isEmoji(word)) {
                    parseEmoji(word);
                }else{
                    if(isGrotesqueWord(word)){
                        parseGrotesqueWord(word);
                    } else if(isKischWord(word)) {
                        parseKischWord(word);
                    } else {
                        parseWord(word);
                    }
                }
            }
        }
    }

    /**
     * Thsi method is used to extract all the emojis from a word. When Splitting a text with the " " condition, there
     * will be times when a couple of emojis comes togheter, that why this methods uses teh wile loop to extract all
     * of them. Then, the method will call the database and check if it is present, to add one to the count of the emoji,
     * or save the new emoji
     * @param text The text of the tweet
     */
    @Override
    public void parseEmoji(String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher emojiMatcher = pattern.matcher(text);
        if (emojiMatcher.find()) {
            while(!emojiMatcher.hitEnd()){
                Word newWord = isWordPresent(emojiMatcher.group());
                if (newWord != null) {
                    newWord.setCount(newWord.getCount() + 1);
                } else {
                    newWord = new Word(emojiMatcher.group(), 1, SyntaxEnum.EMOJI);
                }
                wordRepository.save(newWord);
                emojiMatcher.find();
            }
        }
    }

    @Override
    public void parseKischWord(String text) {
        Word newWord = isWordPresent(text);
        if (newWord != null) {
            newWord.setCount(newWord.getCount() + 1);
        } else {
            newWord = new Word(text, 1, SyntaxEnum.KITSCH);
            wordRepository.save(newWord);
        }
    }

    @Override
    public void parseGrotesqueWord(String text) {
        Word newWord = isWordPresent(text);
        if (newWord != null) {
            newWord.setCount(newWord.getCount() + 1);
        } else {
            newWord = new Word(text, 1, SyntaxEnum.GROTESQUE);
            wordRepository.save(newWord);
        }
    }

    @Override
    public boolean isKischWord(String words) {
        return kischcWords.contains(words);
    }

    @Override
    public boolean isGrotesqueWord(String word) {
        return grotesqueWords.contains(word);
    }

    @Override
    public List<Word> getTop20Words() {
        return wordRepository.findTop20ByOrderByCountDesc();
    }

    @Override
    public List<Word> getTop5Words() {
        return wordRepository.findTop5ByOrderByCountDesc();
    }


    /**
     * This metod is used to save the words in the database. It checks if the word is present in the database, if it is
     * it will add one to the count, if not it will save the new word.
     * @param text The text of the tweet
     */
    @Override
    public void parseWord(String text){
        SyntaxEnum syntaxEnum = getTypeOfWord(text);
        if (syntaxEnum != SyntaxEnum.NONE) {
            try {
                Word newWord = isWordPresent(text);
                if (newWord != null) {
                    newWord.setCount(newWord.getCount() + 1);
                } else {
                    newWord = new Word(text, 1, syntaxEnum);
                }
                wordRepository.save(newWord);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * This method checks the type of word, using the StanfordCoreNLP.
     * @param word The word to be analyzed
     * @return The type of word, if it is one of the valids types of word, otherwise, it will return NONE
     */
    @Override
    public synchronized SyntaxEnum getTypeOfWord(String word) {
        CoreDocument coreDocument = new CoreDocument(word);
        if(!loadedPipeline){
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList = coreDocument.tokens();
        for (CoreLabel coreLabel : coreLabelList) {
            String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            switch (pos) {
                case "NN":
                case "NNS":
                case "NNP":
                case "NNPS":
                    return SyntaxEnum.NOUN;
                case "JJ":
                case "JJR":
                case "JJS":
                    return SyntaxEnum.ADJECTIVE;
                case "VB":
                case "VBG":
                case "VBN":
                case "VBP":
                case "VBZ":
                    return SyntaxEnum.VERB;
                case "RB":
                case "RBR":
                case "RBS":
                case "WRB":
                    return SyntaxEnum.ADVERB;
            }
        }
        return SyntaxEnum.NONE;
    }


    /**
     * THis method is used to check if a word is present in the database
     * @param word the word to be checked
     * @return the word if it is present, null otherwise
     */
    @Override
    public Word isWordPresent(String word) {
        return StreamSupport.stream(wordRepository.findAll().spliterator(), false)
                .filter(w -> w.getWord().equals(word)).findAny().orElse(null);
    }

    /**
     * This method is used to determine if a word of the text is an emoji (Unicode Character I.E.: \uD83D\uDE00)
     * @param word the word to be checked
     * @return true if the word is an emoji, false otherwise
     */
    @Override
    public boolean isEmoji(String word) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(word).find();

    }


    public HashSet<String> readFiles(String fileName) {
        String path = System.getProperty("user.dir")+ File.separator+"src"+File.separator
                +"main"+File.separator+"resources"+File.separator+"static"+File.separator+"files"+File.separator+fileName;
        System.out.println(path);
        try {
            File fichero =  ResourceUtils.getFile("classpath:"+fileName+".dat");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero));
            return (HashSet<String>) ois.readObject();
        } catch (IOException e) {
            System.out.println("The file was not found");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("The file was not found");
    }

    public Word getTopSyntaxEnum(SyntaxEnum syntaxEnum) {
        return wordRepository.findTop1BySyntaxOrderByCountDesc(syntaxEnum);
    }

    public List<Word> getTop10WordsBySyntax(SyntaxEnum syntaxEnum) {
        return wordRepository.findTop10BySyntaxOrderByCountDesc(syntaxEnum);
    }




}

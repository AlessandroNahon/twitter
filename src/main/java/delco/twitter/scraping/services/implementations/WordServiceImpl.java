package delco.twitter.scraping.services.implementations;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiTrie;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.model_content.Emojis;
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
    private Set<String> kischcWords;
    private Set<String> grotesqueWords;

    @Autowired
    private EntityManager em;
    private boolean loadedPipeline = false;
    private boolean loadedFiles = false;
    private final Thread thread = this;

    public WordServiceImpl(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        new Thread(new Runnable() {
            @Override
            public void run() {
                stanfordCoreNLP = Pipeline.getPipeline();
                loadedPipeline = true;
                System.out.println("Pipeline loaded");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                kischcWords = readFiles("kische");
                grotesqueWords = readFiles("grotesque");
                loadedFiles = true;
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
        List<String> emojisList = getAllEmojisFromText(text);
        if(emojisList.size() != 0){
            for(String s : emojisList){
                parseEmoji(s);
            }
        }
        String textWithoutEmojis = EmojiParser.removeAllEmojis(text);
        String[] words = textWithoutEmojis.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && !word.contains("@") && !word.contains("http")) {
                word = word.replace(",", "").replaceAll("(?s)(?<=&lt;).*?(?=&gt;)", "")
                        .replace("\n"," ")
                        .replace(".", "")
                        .replace("?", "")
                        .replace("!", "")
                        .replace("\"","");
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

    /**
     * Thsi method is used to extract all the emojis from a word. When Splitting a text with the " " condition, there
     * will be times when a couple of emojis comes togheter, that why this methods uses teh wile loop to extract all
     * of them. Then, the method will call the database and check if it is present, to add one to the count of the emoji,
     * or save the new emoji
     * @param text The text of the tweet
     */
    @Override
    public void parseEmoji(String text) {
        Word newWord = isWordPresent(text);
        if (newWord != null) {
            newWord.setCount(newWord.getCount() + 1);
        } else {
            newWord = Word.builder().word(text).count(1).syntax(TypeEnum.EMOJI).build();
        }
        wordRepository.save(newWord);
    }

    @Override
    public void parseKischWord(String text) {
        Word newWord = isWordPresent(text);
        if (newWord != null) {
            newWord.setCount(newWord.getCount() + 1);
        } else {
            newWord = Word.builder().word(text.toLowerCase()).count(1).syntax(TypeEnum.KITSCH).build();
            wordRepository.save(newWord);
        }
    }

    @Override
    public void parseGrotesqueWord(String text) {
        Word newWord = isWordPresent(text);
        if (newWord != null) {
            newWord.setCount(newWord.getCount() + 1);
        } else {
            newWord = Word.builder().word(text.toLowerCase()).count(1).syntax(TypeEnum.GROTESQUE).build();
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


    /**
     * This metod is used to save the words in the database. It checks if the word is present in the database, if it is
     * it will add one to the count, if not it will save the new word.
     * @param text The text of the tweet
     */
    @Override
    public void parseWord(String text){
        TypeEnum typeEnum = getTypeOfWord(text);
        if (typeEnum != TypeEnum.NONE) {
            try {
                Word newWord = isWordPresent(text);
                if (newWord != null) {
                    newWord.setCount(newWord.getCount() + 1);
                } else {
                    newWord = Word.builder().word(text.toLowerCase()).count(1).syntax(typeEnum).build();
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
    public synchronized TypeEnum getTypeOfWord(String word) {
        CoreDocument coreDocument = new CoreDocument(word);
        while(!loadedPipeline || !loadedFiles){
            try {
                this.sleep(200);
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
                    return TypeEnum.NOUN;
                case "JJ":
                case "JJR":
                case "JJS":
                    return TypeEnum.ADJECTIVE;
                case "VB":
                case "VBG":
                case "VBN":
                case "VBP":
                case "VBZ":
                    return TypeEnum.VERB;
                case "RB":
                case "RBR":
                case "RBS":
                case "WRB":
                    return TypeEnum.ADVERB;
            }
        }
        return TypeEnum.NONE;
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

    @Override
    public List<String> getAllEmojisFromText(String text) {
        char[] inputCharArray = text.toCharArray();
        List<Emojis> candidates = new ArrayList();
        for(int i = 0; i < text.length(); ++i) {
            int emojiEnd = getEmojiEndPos(inputCharArray, i);
            if (emojiEnd != -1) {
                Emoji emoji = EmojiManager.getByUnicode(text.substring(i, emojiEnd));
                String fitzpatrickString = emojiEnd + 2 <= text.length() ? new String(inputCharArray, emojiEnd, 2) : null;
                Emojis candidate = new Emojis(emoji, fitzpatrickString, i);
                candidates.add(candidate);
                i = candidate.getFitzpatrickEndIndex() - 1;
            }
        }
        List<String> emojis = new ArrayList<>();
        for (Emojis c : candidates){
            emojis.add(c.getEmoji().getUnicode());
        }
        return emojis;
    }


    @Override
    public int getEmojiEndPos(char[] text, int startPos) {
        int best = -1;
        for(int j = startPos + 1; j <= text.length; ++j) {
            EmojiTrie.Matches status = EmojiManager.isEmoji(Arrays.copyOfRange(text, startPos, j));
            if (status.exactMatch()) {
                best = j;
            } else if (status.impossibleMatch()) {
                return best;
            }
        }
        return best;
    }







    public HashSet<String> readFiles(String fileName) {
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

    public Word getTopSyntaxEnum(TypeEnum typeEnum) {
        return wordRepository.findTop1BySyntaxOrderByCountDesc(typeEnum);
    }

    public List<Word> getTop10WordsBySyntax(TypeEnum typeEnum) {
        return wordRepository.findTop10BySyntaxOrderByCountDesc(typeEnum);
    }


    @Override
    public List<Word> getTop20Words() {
        return wordRepository.findTop20ByOrderByCountDesc();
    }

    @Override
    public List<Word> getTop5Words() {
        return wordRepository.findTop5ByOrderByCountDesc();
    }





}

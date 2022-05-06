package delco.twitter.scraping.services.implementations;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiTrie;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.twitterapi.model_content.Emojis;
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
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordServiceImpl extends Thread  implements WordService {

    @Autowired
    private WordRepository wordRepository;

    private StanfordCoreNLP stanfordCoreNLP;
    private Set<String> kischcWords;
    private Set<String> grotesqueWords;
    private Set<String> grotesqueEmoji;
    private boolean loadedPipeline = false;
    private boolean loadedFiles = false;
    long idHelper = 0;


    public static final String TWEET_BELONGS_TO = "Tweet";
    public static final String REPLY_BELONGS_TO = "Reply";

    public WordServiceImpl(){
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
                kischcWords = readFiles("acceptedFilesDat"
                        +File.separator+"kistchWords");
                grotesqueWords = readFiles("notAcceptedFilesDat"
                        +File.separator+"grotesqueWords");
                grotesqueEmoji = readFiles("notAcceptedFilesDat"
                        +File.separator+"grotesqueEmoji");
                loadedFiles = true;
            }
        }).start();

    }

    /**
     * This method is used to read the files from the resources folder that contains all the Kistch/Grotesque
     * words, in order to find them later in the Tweet search
     * @param fileName Name of the file into the resource folder, without the .dat extension
     * @return HashSet of String with al the words
     */
    public HashSet<String> readFiles(String fileName) {
        try {
            File fichero =  ResourceUtils.getFile("classpath:wordsList"+File.separator+fileName+".dat");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero));
            return (HashSet<String>) ois.readObject();
        } catch (IOException e) {
            System.out.println("The file was not found");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("The file was not found");
    }


    /**
     * This method is used to analyze the text of the Tweet. It first check if the text contains emojis. If it has,
     * the algorithm store them into the database, and then delete all of them from the original text. Then, that text
     * is analyzed once again, word by word, to check whether is a Grotesque/Kistch word or it is a valid Word
     * (Noun, adverb, verb, adjective). Once each word is assigned to its corresponding syntax, it is stored into the
     * database
     * @param text The text of the tweet
     */
    @Override
    public void analyzeText(String text, String belongs_to) {
        List<String> emojisList = getAllEmojisFromText(text);
        if(emojisList.size() != 0){
            for(String s : emojisList){
                if(isGrotesqueEmoji(s)){
                    parseWord(s, TypeEnum.GROTESQUE_EMOJI, belongs_to);
                }else{
                    parseWord(s, TypeEnum.KITSCH_EMOJI, belongs_to);
                }
            }
        }
        String textWithoutEmojis = EmojiParser.removeAllEmojis(text);
        String[] words = textWithoutEmojis.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && !word.contains("@") && !word.contains("http") && !word.contains("#")) {
                if(!word.equalsIgnoreCase("the")){
                    word = word.replaceAll("\\p{Punct}","");
                    if(isGrotesqueWord(word)){
                        parseWord(word, TypeEnum.GROTESQUE,belongs_to);
                    } else if(isKischWord(word)) {
                        parseWord(word, TypeEnum.KITSCH,belongs_to);
                    } else {
                        parseWord(word,getTypeOfWord(word),belongs_to);
                    }
                }
                }

            }

        }

    /**
     * This method is used to check if a word is stored into the HashSet of Kitsch words
     * @param word The word to check
     * @return True if the word exists into the Kitsch file, false otherwise
     */
    @Override
    public boolean isKischWord(String word) {
        return kischcWords.contains(word);
    }

    /**
     * This method is used to check if a word is stored into the HashSet of Grotesque words
     * @param word The word to check
     * @return True if the word exists into the Grotesque file, false otherwise
     */
    @Override
    public boolean isGrotesqueWord(String word) {
        return grotesqueWords.contains(word);
    }

    @Override
    public boolean isGrotesqueEmoji(String word) {
        return grotesqueEmoji.contains(word);
    }


    /**
     * This metod is used to save the words in the database. It checks if the word is present in the database, if it is
     * it will add one to the count, if not it will save the new word.
     * @param text The text of the tweet
     */
    @Override
    public synchronized void parseWord(String text, TypeEnum syntax, String belongs_to){
        if(syntax != TypeEnum.GROTESQUE_EMOJI && syntax != TypeEnum.KITSCH_EMOJI
                && syntax != TypeEnum.GROTESQUE && syntax != TypeEnum.KITSCH){
            syntax = getTypeOfWord(text);
        }
        if (syntax != TypeEnum.NONE) {
            try {
                Word newWord = getByWordAndBelongsTo(text,belongs_to);
                if (newWord != null) {
                    newWord.setCount(newWord.getCount() + 1);
                } else {
                    newWord = Word.builder().word(text.toLowerCase())
                            .count(1)
                            .syntax(syntax)
                            .belongsTo(belongs_to)
                            .build();
                }
                wordRepository.save(newWord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Word> getAllWordsFromTweet(Tweet t) {
        String text = t.getText();
        for(Reply r : t.getReplies()) {
            text = text.concat(" ").concat(r.getText());
        }
        return getCountOfWordsFromText(text);
    }

    @Override
    public List<Word> getCountOfWordsFromText(String text) {
        List<Word> finalListOfWords = new ArrayList<>();
        Map<String, Integer> wordList = new HashMap<>();
        for(String s : getAllEmojisFromText(text)){
            if(wordList.containsKey(s)){
                wordList.put(s, wordList.get(s)+1);
            }else{
                wordList.put(s, 1);
            }
        }
        for(Map.Entry e : wordList.entrySet()){
            finalListOfWords.add(Word.builder()
                    .word(e.getKey().toString())
                    .count(Integer.parseInt(e.getValue().toString()))
                    .syntax(TypeEnum.EMOJI)
                    .id(idHelper).build());
            idHelper++;
        }
        wordList.clear();
        String textWithoutEmojis = EmojiParser.removeAllEmojis(text);
        String[] words = textWithoutEmojis.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && !word.contains("@") && !word.contains("http") && !word.contains("#")) {
                word = word.replaceAll("\\p{Punct}","");
                if(wordList.containsKey(word)){
                    wordList.put(word.toLowerCase(), wordList.get(word.toLowerCase())+1);
                }else{
                    wordList.put(word.toLowerCase(), 1);
                }
            }
        }
        for(Map.Entry e : wordList.entrySet()){
            if(isGrotesqueWord(e.getKey().toString())){
                finalListOfWords.add(Word.builder()
                        .word(e.getKey().toString())
                        .count(Integer.parseInt(e.getValue().toString()))
                        .syntax(TypeEnum.GROTESQUE)
                        .id(idHelper).build());
            }else if(isKischWord(e.getKey().toString())){
                finalListOfWords.add(Word.builder()
                        .word(e.getKey().toString())
                        .count(Integer.parseInt(e.getValue().toString()))
                        .syntax(TypeEnum.KITSCH)
                        .id(idHelper).build());
            }else{
                finalListOfWords.add(Word.builder()
                        .word(e.getKey().toString())
                        .count(Integer.parseInt(e.getValue().toString()))
                        .syntax(TypeEnum.NOUN)
                        .id(idHelper).build());
            }
            idHelper++;
        }
        wordList.clear();
        return finalListOfWords;
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
     * This method is copied from the EmojiParser library. It is used to detect all the emojis in a String. The difference
     * between the original method and this one, is that this method returns a list with all the unicode characteres
     * to be treated like a common word into the database
     * @param text The text to analyze
     * @return List of Strings, each string is a different emoji. Returns an empty list if there's no emojis in
     * the text
     */
    @Override
    public List<String> getAllEmojisFromText(String text) {
        char[] inputCharArray = text.toCharArray();
        List<Emojis> candidates = new ArrayList();
        for(int i = 0; i < text.length(); ++i) {
            int emojiEnd = getEmojiEndPos(inputCharArray, i);
            if (emojiEnd != -1) {
                Emoji emoji = EmojiManager.getByUnicode(text.substring(i, emojiEnd));
                String fitzpatrickString = emojiEnd + 2 <= text.length() ?
                        new String(inputCharArray, emojiEnd, 2) : null;
                Emojis candidate = new Emojis(emoji, fitzpatrickString, i);
                candidates.add(candidate);
                i = candidate.getFitzpatrickEndIndex() - 1;
            }
        }
        return candidates.stream().map(Emoji -> Emoji.getEmoji().getUnicode()).collect(Collectors.toList());
    }


    /**
     * This method is used by the getAllEmojisFromText to get the position of the emoji in the String
     * @param text The piece of the text that we're analyzing
     * @param startPos The starting position of the unicode character
     * @return The last position of the unicodeCharacter
     */
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

    @Override
    public List<Word> sortByCountFilterBySyntax(List<Word> listOfWords, TypeEnum ... target) {
        List<Word> sortedFilteredWords = new ArrayList<>();
        if(target.length == 1){
            sortedFilteredWords = listOfWords.stream().filter(word -> word.getSyntax() == target[0])
                    .collect(Collectors.toList());
        }else if(target.length == 2){
            sortedFilteredWords = listOfWords.stream().filter(word -> word.getSyntax() == target[0]
                    || word.getSyntax() == target[1]).collect(Collectors.toList());
        }else if(target.length == 3){
            sortedFilteredWords = listOfWords.stream().filter(word -> word.getSyntax() == target[0]
                    || word.getSyntax() == target[1] || word.getSyntax() == target[2])
                    .collect(Collectors.toList());
        }else{
            System.out.println("This method only support up to three TypeEnum. The list will be empty");
        }
        return sortedFilteredWords;
    }

    @Override
    public List<Word> getAllWordsByBelongTo(String belong_to) {
        return wordRepository.findAllByBelongsTo("Tweet");
    }

    @Override
    public List<Word> getTop20WordsByBelongsToBySyntax(String belongs_to, TypeEnum syntax) {
        return wordRepository.findTop20ByBelongsToAndSyntaxOrderByCountDesc(belongs_to, TypeEnum.NOUN);
    }

    @Override
    public List<Word> getTop10ByBelongsToBySyntax(String belongs_to, TypeEnum syntax) {
        return wordRepository.findTop10ByBelongsToAndSyntaxOrderByCountDesc(belongs_to,syntax);
    }

    @Override
    public Word getTopByBelongsToBySyntax(String belongs_to, TypeEnum syntax) {
        return wordRepository.findTop1ByBelongsToAndSyntaxOrderByCountDesc(belongs_to,syntax);
    }

    @Override
    public Word getByWordAndBelongsTo(String word, String belongs_to) {
        return wordRepository.findByWordAndBelongsTo(word,belongs_to);
    }

    @Override
    public Word getTopEmojiByBelongsTo(String belongs_to) {
        return wordRepository.findTopEmojiByBelongsTo(belongs_to);
    }

    @Override
    public List<Word> getTop20ByBelongsTo(String belongs_to) {
        return wordRepository.findTop20ByBelongsToOrderByCountDesc(belongs_to);
    }

    @Override
    public List<Word> getTop5Words() {
        return wordRepository.findTop5ByOrderByCountDesc();
    }


}

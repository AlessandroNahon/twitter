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
import java.util.stream.StreamSupport;

@Service
public class WordServiceImpl extends Thread  implements WordService {

    @Autowired
    private WordRepository wordRepository;

    private StanfordCoreNLP stanfordCoreNLP;
    private Set<String> kischcWords;
    private Set<String> grotesqueWords;
    private Set<String> kischEmoji;
    private Set<String> grotesqueEmoji;
    private boolean loadedPipeline = false;
    private boolean loadedFiles = false;
    long idHelper = 0;

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
                kischcWords = readFiles("kische");
                grotesqueWords = readFiles("grotesque");
                kischEmoji = readFiles("kischtEmoji");
                grotesqueEmoji = readFiles("grotesqueEmoji");
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
                word = word.replace(",", "").replaceAll("(?s)(?<=&lt;).*?(?=&gt;)", "")
                        .replace("\n"," ")
                        .replace(".", "")
                        .replace("?", "")
                        .replace("!", "")
                        .replace("\"","")
                        .replace("'","")
                        .replace(":","");
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
                Word newWord = isWordPresent(text);
                if (newWord != null) {
                    newWord.setCount(newWord.getCount() + 1);
                } else {
                    newWord = Word.builder().word(text.toLowerCase())
                            .count(1)
                            .syntax(syntax)
                            .belongs_to(belongs_to)
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
                word = word.replace(",", "").replaceAll("(?s)(?<=&lt;).*?(?=&gt;)", "")
                        .replace("\n"," ")
                        .replace(".", "")
                        .replace("?", "")
                        .replace("!", "")
                        .replace("\"","")
                        .replace("'","")
                        .replace(":","");
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
        List<String> emojis = new ArrayList<>();
        for (Emojis c : candidates){
            emojis.add(c.getEmoji().getUnicode());
        }
        return emojis;
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


    /**
     * This method is used to find the mos used word corresponding to a certain TypeEnum
     * @param typeEnum The TypeEnum to do the search
     * @return The word that appears the most within that group
     */
    public Word getTopSyntaxEnum(TypeEnum typeEnum) {
        return wordRepository.findTop1BySyntaxOrderByCountDesc(typeEnum);
    }

    /**
     * This method is used to find the 10 most used words corresponding to a certain TypeEnum
     * @param typeEnum The TypeEnum to do the search
     * @return List<Word> that appears the most within that group
     */
    @Override
    public List<Word> getTop10WordsBySyntax(TypeEnum typeEnum) {
        return wordRepository.findTop10BySyntaxOrderByCountDesc(typeEnum);
    }

    /**
     * This method is used to get a list of the 20 most used words
     * @return List of Word object that has the biggest value "count" in te database
     */
    @Override
    public List<Word> getTop20Words() {
        return wordRepository.findTop20ByOrderByCountDesc();
    }

    /**
     * This method is used to get a list of the 5 most used words
     * @return List of Word object that has the biggest value "count" in te database
     */
    @Override
    public List<Word> getTop5Words() {
        return wordRepository.findTop5ByOrderByCountDesc();
    }





}

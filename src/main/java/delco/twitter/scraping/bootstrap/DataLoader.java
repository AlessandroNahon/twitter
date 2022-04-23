package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.implementations.SentimentServiceImpl;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import delco.twitter.scraping.services.implementations.TwitterAPIServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Map;


@Component class DataLoader implements CommandLineRunner {

    public int maxResults = 20;
    private final TweetRepository tweetRepository;
    private final WordRepository wordRepository;

    private final SentimentServiceImpl sentimentService;
    private final TwitterAPIServiceImpl twitterAPIService;
    private final WordServiceImpl thesaurusService;
    private final ArrayList<Tweet> tweetSet = new ArrayList<>();

    DataLoader(TweetRepository tweetRepository, WordRepository wordRepository, SentimentServiceImpl sentimentService,
               TwitterAPIServiceImpl twitterAPIService, WordServiceImpl thesaurusService) {
        this.tweetRepository = tweetRepository;
        this.wordRepository = wordRepository;
        this.sentimentService = sentimentService;
        this.twitterAPIService = twitterAPIService;
        this.thesaurusService = thesaurusService;
    }


    @Override
    public void run(String... args) throws Exception {
        try {
            tweetRepository.deleteAll();
            Date fechaLimite = new Date(2022-1900,3,21);
            twitterAPIService.getTweets("Greenpeace",fechaLimite);
            generateWords();
            System.out.println("Tweets cargados");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getCause() + " " + e.getMessage());
        }

    }




    public void generateWords() {
        for (Map.Entry entry : thesaurusService.sortWords().entrySet()) {
            if (Integer.parseInt(entry.getValue().toString()) > 1) {
                Word word = new Word();
                word.setWord(String.valueOf(entry.getKey()));
                word.setCount(Integer.parseInt(entry.getValue().toString()));
                wordRepository.save(word);
            } else {
                return;
            }
        }
    }
}








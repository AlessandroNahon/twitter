package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Root;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.repositories.ThesaurusRepository;
import delco.twitter.scraping.services.implementations.SentimentServiceImpl;
import delco.twitter.scraping.services.implementations.ThesaurusServiceImpl;
import delco.twitter.scraping.services.implementations.TwitterAPIServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Map;


@Component class DataLoader implements CommandLineRunner {

    public int maxResults = 20;
    private final TweetRepository tweetRepository;
    private final ThesaurusRepository wordRepository;

    private final SentimentServiceImpl sentimentService;
    private final TwitterAPIServiceImpl twitterAPIService;
    private final ThesaurusServiceImpl thesaurusService;
    private final ArrayList<Tweet> tweetSet = new ArrayList<>();

    DataLoader(TweetRepository tweetRepository, ThesaurusRepository thesaurusRepository, SentimentServiceImpl sentimentService,
               TwitterAPIServiceImpl twitterAPIService, ThesaurusServiceImpl thesaurusService) {
        this.tweetRepository = tweetRepository;
        this.wordRepository = thesaurusRepository;
        this.sentimentService = sentimentService;
        this.twitterAPIService = twitterAPIService;
        this.thesaurusService = thesaurusService;
    }


    @Override
    public void run(String... args) throws Exception {
        try {
            tweetRepository.deleteAll();
            Date fechaLimite = new Date(2022-1900,3,18);
            twitterAPIService.getTweets("Greenpeace",fechaLimite);
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








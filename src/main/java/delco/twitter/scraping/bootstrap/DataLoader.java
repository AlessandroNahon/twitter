package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.repositories.SentimentRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.implementations.SentimentServiceImpl;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import delco.twitter.scraping.services.implementations.TwitterAPIServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component class DataLoader implements CommandLineRunner {

    private final TweetRepository tweetRepository;
    private final WordRepository wordRepository;
    private final SentimentRepository sentimentRepository;

    private final SentimentServiceImpl sentimentService;
    private final TwitterAPIServiceImpl twitterAPIService;
    private final WordServiceImpl wordService;

    DataLoader(TweetRepository tweetRepository, WordRepository wordRepository, SentimentRepository sentimentRepository, SentimentServiceImpl sentimentService,
               TwitterAPIServiceImpl twitterAPIService, WordServiceImpl wordService) {
        this.tweetRepository = tweetRepository;
        this.wordRepository = wordRepository;
        this.sentimentRepository = sentimentRepository;
        this.sentimentService = sentimentService;
        this.twitterAPIService = twitterAPIService;
        this.wordService = wordService;
    }


    @Override
    public void run(String... args) throws Exception {
        try {
            tweetRepository.deleteAll();
            wordRepository.deleteAll();
            Date fechaLimite = new Date(2022-1900, Calendar.MARCH,23);
            twitterAPIService.getTweets("Greenpeace",fechaLimite);
            System.out.println("Tweets cargados");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getCause() + " " + e.getMessage());
        }

    }

    public void filterEmojis(String tweet) {
        Pattern emoji = Pattern.compile("[\\x{10000}-\\x{10FFFF}]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(tweet);
            if (emojiMatcher.find()) {
                while(!emojiMatcher.hitEnd()){
                    System.out.println("Emoji found: " + emojiMatcher.group());
                    emojiMatcher.find();
                }

        }
    }




}








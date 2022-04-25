package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.model.Sentiment;
import delco.twitter.scraping.repositories.SentimentRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.implementations.TweetServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component class DataLoader implements CommandLineRunner {


    private final TweetServiceImpl tweetService;

    private final TweetRepository tweetRepository;
    private final WordRepository wordRepository;
    private final SentimentRepository sentimentRepository;

    public DataLoader(TweetServiceImpl tweetService, TweetRepository tweetRepository, WordRepository wordRepository, SentimentRepository sentimentRepository) {
        this.tweetService = tweetService;

        this.tweetRepository = tweetRepository;
        this.wordRepository = wordRepository;
        this.sentimentRepository = sentimentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
//            Date fechaLimite = new Date(2022-1900, Calendar.MARCH,18);
//
//            limpiarRegistros();
//            tweetService.getUserTimeline("Greenpeace", fechaLimite);
            System.out.println("Tweets cargados");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getCause() + " " + e.getMessage());
        }

    }


    public void limpiarRegistros(){
        tweetRepository.deleteAll();
        wordRepository.deleteAll();
        Iterable<Sentiment> sentiment = sentimentRepository.findAll();
        for(Sentiment s : sentiment){
            s.setAppearances(0);
            sentimentRepository.save(s);
        }
    }




}








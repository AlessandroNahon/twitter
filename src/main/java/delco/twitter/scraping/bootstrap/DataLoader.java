package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.model.Sentiment;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.SentimentRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.implementations.TweetServiceImpl;
import delco.twitter.scraping.services.implementations.VisionAPIServiceImpl;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;


@Component class DataLoader implements CommandLineRunner {


    private final TweetServiceImpl tweetService;

    private final TweetRepository tweetRepository;
    private final ImageRepository imageRepository;
    private final WordRepository wordRepository;
    private final SentimentRepository sentimentRepository;

    @Autowired
    private  VisionAPIServiceImpl visionAPIService;

    @Autowired
    private TwitterAPIService twitterAPIService;


    public DataLoader(TweetServiceImpl tweetService, TweetRepository tweetRepository, ImageRepository imageRepository,
                      WordRepository wordRepository, SentimentRepository sentimentRepository) {
        this.tweetService = tweetService;
        this.tweetRepository = tweetRepository;
        this.imageRepository = imageRepository;
        this.wordRepository = wordRepository;
        this.sentimentRepository = sentimentRepository;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
         try {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    limpiarRegistros();
//                    executeSearch();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void limpiarRegistros(){
        tweetRepository.deleteAll();
        wordRepository.deleteAll();
        imageRepository.deleteAll();
        Iterable<Sentiment> sentiment = sentimentRepository.findAll();
        for(Sentiment s : sentiment){
            s.setAppearances(0);
            sentimentRepository.save(s);
        }
    }

    public void executeSearch(){
        limpiarRegistros();
        String endDate = "2021-06-30"+"T00:00:00-00:00";
        String startDate = "2021-06-10"+"T00:00:00-00:00";
        Root r = twitterAPIService.getTweets("Peta",startDate,endDate);
        tweetService.parseTweetDatumFromRoot(r, "Peta");
        while(r!=null){
            r = twitterAPIService.getNextTweets(r.getMeta().getNext_token(),startDate,endDate);
            tweetService.parseTweetDatumFromRoot(r, "Peta");
        }
        System.out.println("Tweets cargados");
    }

    public void initiateProgram(){
        try {
            URI uri = new URI("https://localhost:8083");
            Desktop.getDesktop().browse(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}








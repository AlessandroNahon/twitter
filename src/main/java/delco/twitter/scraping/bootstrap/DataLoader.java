package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Sentiment;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.*;
import delco.twitter.scraping.services.implementations.TweetServiceImpl;
import delco.twitter.scraping.services.implementations.TwitterAPIServiceImpl;
import delco.twitter.scraping.services.implementations.VisionAPIServiceImpl;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


@Component class DataLoader implements CommandLineRunner {

    @Autowired
    private RepliesService repliesService;

    @Autowired
    private RepliesRepository repliesRepository;

    @Autowired
    private WordService wordService;

    public DataLoader(){

    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {

    }



//    public void limpiarRegistros(){
//        tweetRepository.deleteAll();
//        wordRepository.deleteAll();
//        imageRepository.deleteAll();
//        Iterable<Sentiment> sentiment = sentimentRepository.findAll();
//        for(Sentiment s : sentiment){
//            s.setAppearances(0);
//            sentimentRepository.save(s);
//        }
//    }
//
//    public void executeSearch(String username){
//
//        String startDate = "2021-01-01"+"T00:00:00-00:00";
//        String endDate = "2021-05-01"+"T00:00:00-00:00";
//        Root r = twitterAPIService.getTweets(username,startDate,endDate);
//        tweetService.parseTweetDatumFromRoot(r, username);
//    }
//
//    public void initiateProgram(){
//        try {
//            URI uri = new URI("https://localhost:8083");
//            Desktop.getDesktop().browse(uri);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }




}








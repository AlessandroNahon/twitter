package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.*;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component class DataLoader implements CommandLineRunner {

    @Autowired
    private RepliesService repliesService;

    @Autowired
    private RepliesRepository repliesRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private TwitterAPIService twitterAPIService;

    @Autowired
    private TweetService tweetService;

    public DataLoader(){

    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
//        executeSearch("peta");
//        executeSearch("Greenpeace");
//        executeSearch("WWF");
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
    public void executeSearch(String username){

        String startDate = "2021-01-01"+"T00:00:00-00:00";
        String endDate = "2021-05-01"+"T00:00:00-00:00";
        Root r = twitterAPIService.getTweets(username,startDate,endDate);
        tweetService.parseTweetDatumFromRoot(r, username);
    }
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








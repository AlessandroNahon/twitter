package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Converters.DatumConverters;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;


@Service
public class TweetServiceImpl extends Thread implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private TwitterAPIService twitterAPIService;

    @Autowired
    private RepliesService repliesService;

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private EntityManager em;

    @Autowired
    private DatumConverters datumConverters;

    private final String belogs_to_tweet = "Tweet";

    public TweetServiceImpl() {

    }


    /**
     * This method is used to parse the Root object that comes from the Twitter API and create different
     * tweet objects with the corresponding information about images, replies, sentiment and words.
     * @param root Root object that comes from the Twitter API
     * @param username Username of the user that is being scraped, to set it to the created tweet
     */
    @Override
    public void parseTweetDatumFromRoot(Root root,String username) {
        System.out.println(root.toString());
        root.getData().forEach(datum -> {
                List<Images> images = imageService.getImages(root.getIncludes(),datum);
                if(!images.isEmpty()) {
                    System.out.println("Hay un tweet que es válido");
                    Tweet tweet = datumConverters.convertDatumToTweet(datum);
                    tweetRepository.save(tweet);
                    images.forEach(img -> {
                        img.setTweet(tweet);
                        imageService.saveImageWithTweet(img);
                    });
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            repliesService.parseReplyFromTweet(
                                    twitterAPIService.getReplies(datum.getConversation_id()), tweet);
                        }
                    }).start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wordService.analyzeText(datum.getText(),belogs_to_tweet);
                        }
                    }).start();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        });
    }


    public Set<String> getAllEmojisFromTweets(Tweet t){
        Set<String> emojisList = new HashSet<>(new HashSet<>(wordService.getAllEmojisFromText(t.getText())));
        for(Reply r : t.getReplies()){
            emojisList.addAll(new HashSet<>(wordService.getAllEmojisFromText(r.getText())));
        }
        return emojisList;
    }

    
    /**
     * This method is used by the controller to call the repository and find all the tweets that contains
     * the word passed by parameter
     * @param text The word used in the search
     * @return List with all the Tweets that contains the "text", empty list if there's no tweets containing that text
     */
    @Override
    public List<Tweet> findByText(String text) {
        try{
            return tweetRepository.findAllByTextContaining(text);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
        return new ArrayList<Tweet>();
    }

    
    /**
     * This method is used by the controller to find the last 5 tweets stored in the database. Since the ID is 
     * autoincremental, the query sort by this field and returns the 5 larger ID's 
     * @return List of the last 5 tweets added
     */
    @Override
    public List<Tweet> findTop5ByOrderByIdDesc() {
        return tweetRepository.findTop5ByOrderByIdDesc();
    }
}

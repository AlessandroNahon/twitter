package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Root;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;


@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final WordServiceImpl wordService;
    private final ImageServiceImpl imageService;
    private final APITWServiceImpl twitterAPIService;
    private final RepliesServiceImpl repliesService;
    private final SentimentService sentimentService;
    @Autowired
    private EntityManager em;

    public TweetServiceImpl(APITWServiceImpl twitterAPIService, TweetRepository tweetRepository,
                            WordServiceImpl wordService, ImageServiceImpl imageService,
                            RepliesServiceImpl repliesService, SentimentService sentimentService) {
        this.tweetRepository = tweetRepository;
        this.wordService = wordService;
        this.imageService = imageService;
        this.twitterAPIService = twitterAPIService;
        this.repliesService = repliesService;
        this.sentimentService = sentimentService;
    }


    /**
     * This method is used to parse the Root object that comes from the Twitter API and create different
     * tweet objects with the corresponding information about images, replies, sentiment and words.
     * @param root Root object that comes from the Twitter API
     * @param username Username of the user that is being scraped, to set it to the created tweet
     */
    @Override
    public void parseTweetDatumFromRoot(Root root,String username) {
        root.getData().forEach(datum -> {
            if (!isRetweet(datum.getText()) && containsMedia(datum)) {
                System.out.println("Analiza tweet: " + datum.getText());
                Tweet tweet = Tweet.builder()
                        .text(datum.getText()
                                .replace("&gt;&gt;", "")
                                .replace("&gt+", "")
                                .replace("&gt;",""))
                        .username(username)
                        .createdAt(datum.getCreated_at())
                        .conversationId(datum.getConversation_id()).build();
                tweet.setTextSentiment(sentimentService.getSentiment(datum.getText()));
                imageService.getImages(root.getIncludes(), datum, tweet);
                repliesService.parseReplyFromTweet(
                twitterAPIService.getReplies(datum.getConversation_id(), tweet),tweet);
                tweetRepository.save(tweet);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wordService.analyzeText(datum.getText());
                    }
                }).start();

            }
        });
    }

    /**
     * This method is used to wrap the logic of getting tweets until a certain date. Once the last stored tweet
     * in the database is older than the maxDate, it will stop getting tweets.
     * @param username Username of the user that is being scraped, to set it to the created tweet
     * @param maxDate Date that limits the tweets that will be saved
     */
    @Override
    public void getUserTimeline(String username, Date maxDate) {
        Root raiz = twitterAPIService.getTweets(username, maxDate);
        parseTweetDatumFromRoot(raiz, username);
        boolean fechaLimite = false;
        while (!fechaLimite) {
            try {
                Tweet lastTweet = tweetRepository.findTop1ByOrderByIdDesc();
                if (lastTweet.getCreatedAt().after(maxDate)) {
                    raiz = twitterAPIService.getNextTweets(username, raiz);
                    parseTweetDatumFromRoot(raiz, username);
                } else {
                    fechaLimite = true;
                }
            } catch (NoResultException e) {
                System.out.println(e.getCause());
                System.out.println(e.getLocalizedMessage());
                System.out.println("No se ha guardado todavía ningún tweet");
            }
        }
    }

    
    /**
     * This method is used to check if a tweet is a retweet.
     * @param tweet Tweet to be checked
     * @return True if the tweet contains RT @, which is the standart for a retweet. False otherwise.
     */
    @Override
    public boolean isRetweet(String tweet) {
        return tweet.contains("RT @");
    }

    
    /**
     * This method is used to check if a tweet contains media.
     * @param datum Tweet to be checked
     * @return True if the specific tweet's list of media is not empty, which means it has
     * media keys attached. False otherwise.
     */
    @Override
    public boolean containsMedia(Datum datum) {
        return datum.getAttachments().getMedia_keys().size() != 0;
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

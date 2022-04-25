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
                Tweet tweet = new Tweet();
                tweet.setText(datum.getText());
                tweet.setCreatedAt(datum.getCreated_at());
                tweet.setUsername(username);
                tweet.setConversationId(datum.getConversation_id());
                tweet.setTextSentiment(sentimentService.getSentiment(datum.getText()));
                imageService.setImages(root.getIncludes(), datum, tweet);
                repliesService.parseReplyFromTweet(
                twitterAPIService.getReplies(datum.getConversation_id(), tweet),tweet);
                tweetRepository.save(tweet);
                wordService.analyzeText(datum.getText());
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
                Tweet lastTweet = (Tweet) em.createNamedQuery("Tweet.findLastTweet").getResultList().get(0);
                if (lastTweet.getCreatedAt().before(maxDate)) {
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
}

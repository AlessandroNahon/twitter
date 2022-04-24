package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Root;
import delco.twitter.scraping.repositories.TweetRepository;
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
    @Autowired
    private EntityManager em;

    public TweetServiceImpl(APITWServiceImpl twitterAPIService, TweetRepository tweetRepository,
                            WordServiceImpl wordService, ImageServiceImpl imageService,
                            RepliesServiceImpl repliesService) {
        this.tweetRepository = tweetRepository;
        this.wordService = wordService;
        this.imageService = imageService;
        this.twitterAPIService = twitterAPIService;
        this.repliesService = repliesService;
    }



    @Override
    public void parseTweetDatumFromRoot(Root root, Date maxDate, String username) {
        root.getData().forEach(datum -> {
            if (!isRetweet(datum.getText()) && containsMedia(datum)) {
                System.out.println("Analiza tweet: " + datum.getText());
                Tweet tweet = new Tweet();
                tweet.setText(datum.getText());
                tweet.setCreatedAt(datum.getCreated_at());
                tweet.setUsername(username);
                tweet.setConversationId(datum.getConversation_id());
                //tweet.setTextSentiment(sentimentService.getSentiment(datum.getText()));
                imageService.setImages(root.getIncludes(), datum, tweet);
                repliesService.parseReplyFromTweet(
                twitterAPIService.getReplies(datum.getConversation_id(), tweet),tweet);
                tweetRepository.save(tweet);
                wordService.analyzeText(datum.getText());
            }
        });
    }

    @Override
    public void getUserTimeline(String username, Date maxDate) {
        Root raiz = twitterAPIService.getTweets(username, maxDate);
        parseTweetDatumFromRoot(raiz, maxDate, username);
        boolean fechaLimite = false;
        while (!fechaLimite) {
            try {
                Tweet lastTweet = (Tweet) em.createNamedQuery("Tweet.findLastTweet").getResultList().get(0);
                if (lastTweet.getCreatedAt().before(maxDate)) {
                    raiz = twitterAPIService.getNextTweets(username, raiz);
                    parseTweetDatumFromRoot(raiz, maxDate, username);
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

    @Override
    public boolean isRetweet(String tweet) {
        return tweet.contains("RT @");
    }

    @Override
    public boolean containsMedia(Datum datum) {
        return datum.getAttachments().getMedia_keys().size() != 0;
    }
}

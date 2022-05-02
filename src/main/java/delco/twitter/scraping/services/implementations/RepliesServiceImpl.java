package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Root;
import delco.twitter.scraping.repositories.RepliesRepository;
import delco.twitter.scraping.services.interfaces.ImageService;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class RepliesServiceImpl implements RepliesService {

    @Autowired
    private RepliesRepository repliesRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private WordService wordService;

    @Autowired
    private SentimentService sentimentService;

    public RepliesServiceImpl() {}

    /**
     * This method is used find all the replies gathered from an original tweet, passing the Id of the original tweet
     * @param tweetId Id of the original tweet
     * @return Set of replies
     */
    @Override
    public Iterable<Reply> findByTweetId(Long tweetId) {
        Set<Reply> replies = new HashSet<>();
        repliesRepository.findAll().forEach(reply -> {
           if(reply.getOriginalTweet().getId().equals(tweetId)) {
               replies.add(reply);
           }
        });
        return replies;
    }

    /**
     * This method is used to parse the Root element from the Twitter API, and set the replies to an original tweet
     * @param root Root element from the Twitter API
     * @param originalTweet Original tweet
     */
    @Override
    public void parseReplyFromTweet(Root root, Tweet originalTweet) {
        if (root.getData().size() != 0) {
            for (int i = 0; i < Math.min(root.getData().size(), 10); i++) {
                try {
                    Datum dt = root.getData().get(i);
                    System.out.println("Analiza respuesta: " + dt.getText());
                    Reply reply = new Reply();
                    reply.setText(dt.getText());
                    imageService.getImages(root.getIncludes(),dt).forEach(reply::addImage);
                    reply.setTextSentiment(sentimentService.getSentiment(dt.getText()));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wordService.analyzeText(dt.getText());
                        }
                    }).start();
                    originalTweet.addReply(reply);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                    return;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
    }

    /**
     * This method calls the repository in order to find all the replies that contains a String passed via
     * parameter
     * @param text The text to search into the replies objects
     * @return The list of replies which contains that text
     */
    @Override
    public List<Reply> findAllByTextContaining(String text) {
        return repliesRepository.findAllByTextContaining(text);
    }


}

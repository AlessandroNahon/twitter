package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Image;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Medium;
import delco.twitter.scraping.model.model_content.Root;
import delco.twitter.scraping.repositories.RepliesRepository;
import delco.twitter.scraping.services.interfaces.RepliesService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


@Service
public class RepliesServiceImpl implements RepliesService {

    private final RepliesRepository repliesRepository;
    private final ImageServiceImpl imageService;
    private final WordServiceImpl wordService;

    public RepliesServiceImpl(RepliesRepository repliesRepository, ImageServiceImpl imageService,
                              WordServiceImpl wordService) {
        this.repliesRepository = repliesRepository;
        this.imageService = imageService;
        this.wordService = wordService;
    }

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
                    imageService.setImages(root.getIncludes(), dt, reply);
                    wordService.analyzeText(dt.getText());
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


}

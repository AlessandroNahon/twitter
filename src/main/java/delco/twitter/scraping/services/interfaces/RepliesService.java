package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;

import java.util.List;

public interface RepliesService {

    Iterable<Reply> findByTweetId(Long tweetId);

    void parseReplyFromTweet(Root datum, Tweet originalTweet);

    List<Reply> findAllReplies();

    List<Reply> findByText(String text, String organization);

    List<Reply> getByOrganization(String organization);

    void deleteByTweetId(Long id);

    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    List<Reply> findTextImage(String username, boolean wantPositive);

    List<Reply> findText(String username, boolean wantPositive);

    List<Reply> findTextEmoji(String username, boolean wantPositive);

    List<Reply> findFullMatches(String username, boolean wantPositive);

    List<Reply> findAllOthers(String username);

    List<Reply> getCountBySentiment(String username,boolean wantPositive);


}

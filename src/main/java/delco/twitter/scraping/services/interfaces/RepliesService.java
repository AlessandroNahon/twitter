package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;

import java.util.List;

public interface RepliesService {

    Iterable<Reply> findByTweetId(Long tweetId);

    void parseReplyFromTweet(Root datum, Tweet originalTweet);

    List<Reply> findAllReplies();

    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    // =============================================
    //           FIND POSITIVE CONTENT
    // =============================================

    List<Reply> findAllByTextContaining(String text);

    List<Reply> getReplyPositiveTextAndPositiveImage();

    Integer getCountReplyPositiveTextAndPositiveImage();

    List<Reply> getReplyPositiveTextAndPositiveEmojis();

    Integer getCountReplysPositiveTextAndPositiveEmojis();

    List<Reply> getFullMatchesReply();

    Integer getCountFullMatchesReply();


    // =============================================
    //           FIND NEGATIVE CONTENT
    // =============================================

    List<Reply> getReplyNegativeTextAndNegativeImage();

    Integer getCountReplyNegativeTextAndNegativeImage();

    List<Reply> getReplyNegativeTextAndNegativeEmojis();

    Integer getCountReplysNegativeTextAndNegativeEmojis();

    List<Reply> getFullNegativeMatchesReply();

    Integer getCountFullNegativeMatchesReply();

}

package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.utils.ImageUtil;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.implementations.TweetServiceImpl;
import delco.twitter.scraping.services.interfaces.RepliesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tweet")
public class TweetController {

    private final RepliesService replyService;
    private final TweetServiceImpl tweetService;
    private final TweetRepository tweetRepository;

    public TweetController(RepliesService replyService, TweetServiceImpl tweetService, TweetRepository tweetRepository) {
        this.replyService = replyService;
        this.tweetService = tweetService;
        this.tweetRepository = tweetRepository;
    }

    @RequestMapping("/showTweetInformation/{id}")
    public String showById(@PathVariable String id, Model model) {
        Tweet tweetInfo = tweetRepository.findById(Long.valueOf(id)).get();
        model.addAttribute("replies", replyService.findByTweetId(Long.valueOf(id)));
        model.addAttribute("tweet", tweetInfo);
        model.addAttribute("imgUtil",new ImageUtil());
        model.addAttribute("emojis",tweetService.getAllEmojisFromTweets(tweetInfo));
        return "tweet/showTweetInformation";
    }

}

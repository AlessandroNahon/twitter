package delco.twitter.scraping.controller;

import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReplyController {

    private final RepliesService replyService;
    private final TweetService tweetService;

    public ReplyController(RepliesService replyService, TweetService tweetService) {
        this.replyService = replyService;
        this.tweetService = tweetService;
    }

    @RequestMapping("/replies/showReply/{id}")
    public String showById(@PathVariable String id, Model model) {
        model.addAttribute("replies", replyService.findByTweetId(Long.valueOf(id)));
        model.addAttribute("tweet", tweetService.findById(Long.valueOf(id)));
        return "replies/showReply";
    }

}

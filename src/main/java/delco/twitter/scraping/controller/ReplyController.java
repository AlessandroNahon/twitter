package delco.twitter.scraping.controller;

import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.RepliesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReplyController {

    private final RepliesService replyService;
    private final TweetRepository tweetService;

    public ReplyController(RepliesService replyService, TweetRepository tweetService) {
        this.replyService = replyService;
        this.tweetService = tweetService;
    }

    @RequestMapping("/replies/showReply/{id}")
    public String showById(@PathVariable String id, Model model) {
        model.addAttribute("replies", replyService.findByTweetId(Long.valueOf(id)));
        model.addAttribute("tweet", tweetService.findById(Long.valueOf(id)).get());
        return "replies/showReply";
    }

}

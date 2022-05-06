package delco.twitter.scraping.controller;

import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/sentiment")
public class SentimentController {

    @RequestMapping("/index")
    public String index(Model model) {
        List<Integer> list = tweetService.analyzeDatabase("Sentimental");
        model.addAttribute("numberOfFullPositive",list.get(0));
        model.addAttribute("numberOfNeutralTweets",list.get(1));
        model.addAttribute("numberOfFullNegative",list.get(2));
        return "sentiment/index";
    }


    @Autowired
    private TweetService tweetService;

    @RequestMapping("/fragments/table_search_tweet")
    public String searchIndex(Model model, @RequestParam String search) {
        if(search.equals("Sentimental")){
            List<Integer> list = tweetService.analyzeDatabase("Sentimental");
            model.addAttribute("numberOfFullPositive",list.get(0));
            model.addAttribute("numberOfNeutralTweets",list.get(1));
            model.addAttribute("numberOfFullNegative",list.get(2));
        }
        return "tweet/fragments/table_search_tweet :: table_search_tweet";
    }

}

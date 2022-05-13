package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sentiment")
public class SentimentController {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private RepliesService repliesService;

    @RequestMapping("/index")
    public String index(Model model) {
        return "sentiment/index";
    }

    @RequestMapping("/fragments/cards_fragment")
    public String getFullFragment(Model model, @RequestParam("organization") String organization,
                                  @RequestParam("belongsTo") String belongsTo,
                                  @RequestParam("classification") String classification){
        if(belongsTo.equals("Tweet")){
            if(classification.equals("Grey")){
                List<Tweet> tweetList = tweetService.findAllOthers(organization);
                model.addAttribute("tweets",tweetList.subList(0,Math.min(10,tweetList.size())));
                model.addAttribute("maxPages",tweetList.size()%10 == 0 ? tweetList.size()/10 : tweetList.size()/10 + 1);
                return "sentiment/fragments/table_info :: table_info";
            }else{
                model.addAttribute("sentimentAndImages",tweetService.findTextImage(organization, classification.equals("Sentimental")).size());
                model.addAttribute("sentimentAndEmojis",tweetService.findTextEmoji(organization, classification.equals("Sentimental")).size());
                model.addAttribute("fullSearch",tweetService.findFullMatches(organization, classification.equals("Sentimental")).size());
            }
        }else{
            if(classification.equals("Grey")){
                List<Reply> replyList = repliesService.findAllOthers(organization);
                model.addAttribute("replies",replyList.subList(0,Math.min(10,replyList.size())));
                model.addAttribute("maxPages",replyList.size()%10 == 0 ? replyList.size()/10 : replyList.size()/10 + 1);
                return "sentiment/fragments/table_info :: table_info";
            }else{
                model.addAttribute("sentimentAndImages",repliesService.findTextImage(organization, classification.equals("Sentimental")).size());
                model.addAttribute("sentimentAndEmojis",repliesService.findTextEmoji(organization, classification.equals("Sentimental")).size());
                model.addAttribute("fullSearch",repliesService.findFullMatches(organization, classification.equals("Sentimental")).size());
            }
        }
        return "sentiment/fragments/cards_fragment :: cards_fragment";
    }

    @RequestMapping("/fragments/table_info")
    public String getTableInfo(Model model, @RequestParam("organization") String organization,
    @RequestParam("belongsTo") String belongsTo, @RequestParam("searchType") String searchType,
    @RequestParam("classification") String classification, @RequestParam("page") int page){
        page--;
        if (belongsTo.equals("Tweet")) {
            List<Tweet> tweetList;
            if (classification.equals("Grey")) {
                tweetList = tweetService.findAllOthers(organization);
            } else {
                switch (searchType) {
                    case "Img & Sentiment":
                        tweetList = tweetService.findTextImage(organization, classification.equals("Sentimental"));
                        break;
                    case "Emoji & Sentiment":
                        tweetList = tweetService.findTextEmoji(organization, classification.equals("Sentimental"));
                        break;
                    default:
                        tweetList = tweetService.findFullMatches(organization, classification.equals("Sentimental"));
                        break;
                }
            }
            model.addAttribute("tweets", tweetList.subList(page * 10, Math.min(page * 10 + 10, tweetList.size())));
            model.addAttribute("maxPages", tweetList.size() % 10 == 0 ? tweetList.size() / 10 : tweetList.size() / 10 + 1);
        } else {
            List<Reply> replyList = new ArrayList<>();
            if (classification.equals("Grey")) {
                replyList = repliesService.findAllOthers(organization);
            } else {
                switch (searchType) {
                    case "Img & Sentiment":
                        replyList = repliesService.findTextImage(organization, classification.equals("Sentimental"));
                        break;
                    case "Emoji & Sentiment":
                        replyList = repliesService.findTextEmoji(organization, classification.equals("Sentimental"));
                        break;
                    default:
                        replyList = repliesService.findFullMatches(organization, classification.equals("Sentimental"));
                        break;
                }
            }
            model.addAttribute("replies", replyList.subList(page * 10, Math.min(page * 10 + 10, replyList.size())));
            model.addAttribute("maxPages", replyList.size() % 10 == 0 ? replyList.size() / 10 : replyList.size() / 10 + 1);
        }
        return "sentiment/fragments/table_info :: table_info";
    }

}

package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.utils.ImageUtil;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.RepliesRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.WordService;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private WordService wordService;

    @Autowired
    private TweetService tweetService;

    @Autowired
    private RepliesService repliesService;


    public IndexController(){
    }

    @RequestMapping(value = {"/", "/index" })
    public String loadIndexPage(Model model){
        getSentimentAnlysis(model,"Greenpeace","Tweet");
        return "index";
    }

    @RequestMapping("/indexFragments/whole_index_fragment")
    public String loadIndexFragment(Model model, @RequestParam("organization") String organization, @RequestParam("belongsTo") String belongsTo){
        model.addAttribute("sentiment_dataset", sentimentService.getSentimentsByOrganizationAndBelongs(organization,belongsTo));
        model.addAttribute("imgUtil",new ImageUtil());
        List<Images> imagesList = belongsTo.equals("Reply") ?
                getImagesByListOfReplies(getRepliesByListOfTweets(tweetService.findByUsername(organization)))
                : getImagesByListOfTweets(tweetService.findByUsername(organization));
        model.addAttribute("images", imagesList.subList(0,Math.min(imagesList.size(),10)));
        model.addAttribute("words_dataset",wordService.getSortedByBelongsAndOrganization(belongsTo,organization,
                true,5));
        model.addAttribute("belongsTo",belongsTo);
        model.addAttribute("textTitle",getTextTile(organization,belongsTo));
        model.addAttribute("totalCount",getCountOfTweetsReplies(organization,belongsTo));
        getSentimentAnlysis(model,organization,belongsTo);
        return "indexFragments/whole_index_fragment :: whole_index_fragment";
    }


    private List<Reply> getRepliesByListOfTweets(List<Tweet> tweets) {
        List<Reply> replies = new ArrayList<>();
        tweets.forEach(tweet -> { replies.addAll(tweet.getReplies()); });
        return replies;
    }

    private List<Images> getImagesByListOfReplies(List<Reply> replies) {
        List<Images> images = new ArrayList<>();
        replies.forEach(reply -> { images.addAll(reply.getImages()); });
        return images;
    }

    private List<Images> getImagesByListOfTweets(List<Tweet> tweets) {
        List<Images> images = new ArrayList<>();
        tweets.forEach(tweet -> { images.addAll(tweet.getImages()); });
        return images;
    }

    private String getTextTile(String organization, String belongsTo){
        if(belongsTo.equals("Reply")){
            return "Replies from " + organization;
        }else{
            return "Tweets from " + organization;
        }
    }

    private int getCountOfTweetsReplies(String organization, String belongsTo){
        return belongsTo.equals("Reply") ?
                getRepliesByListOfTweets(tweetService.findByUsername(organization)).size()
                : tweetService.findByUsername(organization).size();
    }


    public void getSentimentAnlysis(Model model, String organization, String belongsTo){
        int total, negative, neutral;
        if(belongsTo.equals("Tweet")){
            total = tweetService.findByUsername(organization).size();
            negative = tweetService.getCountBySentiment(organization, false).size();
            neutral = tweetService.findAllOthers(organization).size();
        }else{
            total = repliesService.getByOrganization(organization).size();
            negative = repliesService.getCountBySentiment(organization, false).size();
            neutral = repliesService.findAllOthers(organization).size();
        }
        model.addAttribute("positive_count", (total - neutral - negative));
        model.addAttribute("negative_count", negative);
        model.addAttribute("gray_count", neutral);

    }


}

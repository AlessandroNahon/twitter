package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.utils.ImageUtil;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.RepliesRepository;
import delco.twitter.scraping.repositories.TweetRepository;
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
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private WordService wordService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private ImageRepository imageRepository;

    public IndexController(){
    }

    @RequestMapping(value = {"/", "/index" })
    public String loadIndexPage(Model model){
        model.addAttribute("sentiment_dataset", sentimentService.findAllSentiment());
        model.addAttribute("imgUtil",new ImageUtil());
        model.addAttribute("images", imageRepository.findByReplyIsNull());
        model.addAttribute("words_dataset",wordService.getTop5Words());
        Long totalPages = (tweetRepository.count()%10)==0?(tweetRepository.count()/10):((tweetRepository.count()/10)+1);
        model.addAttribute("totalPages",totalPages);
        model.addAttribute("actualPage",1);
        model.addAttribute("tweets", tweetRepository.findAll(PageRequest.of(0,10)));
        getSentimentAnlysis(model);
        return "index";
    }


    public void getSentimentAnlysis(Model model){
        List<Integer> listOfValues = sentimentService.analyzeDatabaseByTypeAndClassification("Sentimental");
        int countPositive = listOfValues.get(0)+listOfValues.get(1)+listOfValues.get(2);
        model.addAttribute("numberOfFullPositive",countPositive);
        listOfValues = sentimentService.analyzeDatabaseByTypeAndClassification("Disruptive");
        int countNegative = listOfValues.get(0)+listOfValues.get(1)+listOfValues.get(2);
        model.addAttribute("numberOfFullNegative",countNegative);
        model.addAttribute("numberOfNeutralTweets",
                sentimentService.findAllOtherReply().size()+sentimentService.findAllOtherTweets().size());

    }


}

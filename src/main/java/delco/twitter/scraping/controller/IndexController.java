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
import java.util.Optional;

@Controller
public class IndexController {


    @Autowired
    private TweetService tweetService;

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private WordService wordService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RepliesRepository repliesRepository;

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
//        getAnalysisCount(model);

        return "index";
    }

//    @GetMapping("/indexFragments/tweet_table")
//    public String getPaginationTable(Model model, @RequestParam("page") Optional<Integer> page,
//                                     @RequestParam("totalPages") Optional<Integer> maxPages){
//        int currentPage = page.orElse(1);
//        int totalPages = maxPages.orElse(1);
//        model.addAttribute("numberOfPages",totalPages);
//        model.addAttribute("actualPage",currentPage);
//        model.addAttribute("tweets", tweetRepository.findAll(PageRequest.of(currentPage,10)));
//        return "indexFragments/tweet_table :: tweet_table";
//    }
//
//    private void getAnalysisCount(Model model) {
//        int numberOfFullPositive = repliesRepository.findImageTextPositive()
//                .add(BigInteger.valueOf(tweetRepository.findAllFullPositiveTweets().size())).intValue();
//        int numberOfFullNegative = repliesRepository.findImageTextNegative()
//                .add(BigInteger.valueOf(tweetRepository.findAllFullNegativeTweets().size())).intValue();
//        int numberOfNeutralTweets = repliesRepository.findImageTextGray()
//                .add(BigInteger.valueOf(tweetRepository.findAllOtherTweets().size())).intValue();
//        model.addAttribute("numberOfFullPositive",numberOfFullPositive);
//        model.addAttribute("numberOfFullNegative",numberOfFullNegative);
//        model.addAttribute("numberOfNeutralTweets",numberOfNeutralTweets);
//    }


}

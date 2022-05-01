package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.utils.ImageUtil;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.WordService;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class IndexController {

    private final TweetService tweetService;
    private final TweetRepository tweetRepository;
    private final ImageRepository imageRepository;
    private final SentimentService sentimentService;
    private final WordService wordService;

    public IndexController(TweetService tweetService, TweetRepository tweetRepository,
                           ImageRepository imageRepository, SentimentService sentimentService,
                           WordService wordService) {
        this.tweetService = tweetService;
        this.tweetRepository = tweetRepository;
        this.imageRepository = imageRepository;
        this.sentimentService = sentimentService;
        this.wordService = wordService;
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
        System.out.println(1+"  "+totalPages);

        return "index";
    }

    @GetMapping("/indexFragments/tweet_table")
    public String getPaginationTable(Model model, @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("totalPages") Optional<Integer> maxPages){
        int currentPage = page.orElse(1);
        int totalPages = maxPages.orElse(1);
        model.addAttribute("numberOfPages",totalPages);
        model.addAttribute("actualPage",currentPage);
        model.addAttribute("tweets", tweetRepository.findAll(PageRequest.of(currentPage,10)));
        return "indexFragments/tweet_table :: tweet_table";
    }


}

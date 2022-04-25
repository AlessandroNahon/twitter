package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.ImageUtil;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.WordService;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping(value = {"/", "/index" }, method = {RequestMethod.GET})
    public String listTweets(Model model, @RequestParam("page") Optional<Integer> page){
        int currentPage = page.orElse(1) == 0 ? 1 : page.orElse(1);
        model.addAttribute("numberOfPages",
                (tweetRepository.count()%10)==0?(tweetRepository.count()/10):(tweetRepository.count()/10)+1);
        model.addAttribute("actualPage",currentPage);
        model.addAttribute("tweets", tweetRepository.findAll(PageRequest.of(currentPage-1,10)));
        model.addAttribute("sentiment_dataset", sentimentService.findAllSentiment());
        model.addAttribute("imgUtil",new ImageUtil());
        model.addAttribute("images", imageRepository.findAll(PageRequest.of(1, 10)));
        model.addAttribute("words_dataset",wordService.getTop5Words());
        return "index";
    }
}

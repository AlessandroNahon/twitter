package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.ImageUtil;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.ImageService;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.ThesaurusService;
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
    private final ThesaurusService thesaurusService;
    private final ImageService imageService;

    public IndexController(TweetService tweetService, TweetRepository tweetRepository,
                           ImageRepository imageRepository, SentimentService sentimentService,
                           ThesaurusService thesaurusService, ImageService imageService) {
        this.tweetService = tweetService;
        this.tweetRepository = tweetRepository;
        this.imageRepository = imageRepository;
        this.sentimentService = sentimentService;
        this.thesaurusService = thesaurusService;
        this.imageService = imageService;
    }

    @RequestMapping(value = {"/", "/index" }, method = {RequestMethod.GET})
    public String listTweets(Model model, @RequestParam("page") Optional<Integer> page){
        int currentPage = page.orElse(1) == 0 ? 1 : page.orElse(1);
        model.addAttribute("numberOfPages",tweetRepository.count()/10);
        model.addAttribute("actualPage",currentPage);
        model.addAttribute("tweets", tweetService.findPaginated(PageRequest.of(currentPage, 10)));
        model.addAttribute("sentiment_values", sentimentService.getAppearances());
        Object[] thesaurusValues = thesaurusService. getFiveWords();
        model.addAttribute("imgUtil",new ImageUtil());
        model.addAttribute("images", imageRepository.findAll(PageRequest.of(1, 10)));
        model.addAttribute("t_words", thesaurusValues[0]);
        model.addAttribute("t_values", thesaurusValues[1]);
        return "index";
    }
}

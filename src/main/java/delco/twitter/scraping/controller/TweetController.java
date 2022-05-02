package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.utils.ImageUtil;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tweet")
public class TweetController {


    @Autowired
    private TweetService tweetService;

    @Autowired
    private WordService wordService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private ImageRepository imageRepository;


    public TweetController(){ }

    @RequestMapping("/showTweetInformation/{id}")
    public String showById(@PathVariable String id, Model model) {
        Tweet tweetInfo = tweetRepository.findById(Long.valueOf(id)).get();
        List<Word> listOfWords  = wordService.getAllWordsFromTweet(tweetInfo);
        model.addAttribute("replies", tweetInfo.getReplies());
        model.addAttribute("tweet", tweetInfo);
        model.addAttribute("imgUtil",new ImageUtil());
        model.addAttribute("emojis",tweetService.getAllEmojisFromTweets(tweetInfo));
        model.addAttribute("words",wordService.sortByCountFilterBySyntax(listOfWords, TypeEnum.NOUN));
        model.addAttribute("topKicheWords",wordService.sortByCountFilterBySyntax(listOfWords,TypeEnum.KITSCH));
        model.addAttribute("topGrotesqueWords",wordService.sortByCountFilterBySyntax(listOfWords,TypeEnum.GROTESQUE));

        return "tweet/showTweetInformation";
    }


    @GetMapping("/fragments/display_image_modal")
    public String getImageById(Model model, @RequestParam("id") Optional<Long> id){
        model.addAttribute("picture",imageRepository.findById(id.get()).get() );
        model.addAttribute("imgUtil",new ImageUtil());
        return "tweet/fragments/display_image_modal :: display_image_modal";
    }

    @GetMapping("/fragments/show_reply_images")
    public String getImagesFromReply(Model model, @RequestParam("id") Optional<Long> id){
        model.addAttribute("pictures",imageRepository.findByReplyId(id.get()));
        model.addAttribute("imgUtil",new ImageUtil());
        return "tweet/fragments/show_reply_images :: show_reply_images";
    }


}

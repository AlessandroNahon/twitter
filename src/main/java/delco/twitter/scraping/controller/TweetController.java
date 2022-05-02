package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.utils.ImageUtil;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.implementations.TweetServiceImpl;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Word> listOfWords  = getListOfWords(tweetInfo);
        model.addAttribute("replies", tweetInfo.getReplies());
        model.addAttribute("tweet", tweetInfo);
        model.addAttribute("imgUtil",new ImageUtil());
        model.addAttribute("emojis",tweetService.getAllEmojisFromTweets(tweetInfo));
        model.addAttribute("words",listOfWords.stream().filter(word -> word.getSyntax() == TypeEnum.NOUN ||
                word.getSyntax() == TypeEnum.EMOJI)
                .collect(Collectors.toList()));
        model.addAttribute("topKicheWords",listOfWords.stream().filter(word -> word.getSyntax() == TypeEnum.KITSCH)
                .collect(Collectors.toList()));
        model.addAttribute("topGrotesqueWords",listOfWords.stream().filter(word -> word.getSyntax() == TypeEnum.GROTESQUE)
                .collect(Collectors.toList()));
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

    public List<Word> getListOfWords(Tweet t){
        String text = t.getText();
        for(Reply r : t.getReplies()) {
            text = text.concat(" ").concat(r.getText());
        }
        return wordService.getWordsFromText(text);
    }

}

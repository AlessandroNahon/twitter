package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.services.implementations.RepliesServiceImpl;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/thesaurus")
public class ThesaurusController {

    private final WordServiceImpl wordService;
    private final RepliesServiceImpl repliesService;
    private final TweetService tweetService;

    public ThesaurusController(WordServiceImpl wordService, RepliesServiceImpl repliesService, TweetService tweetService) {
        this.wordService = wordService;
        this.repliesService = repliesService;
        this.tweetService = tweetService;
    }

    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("topNoun", wordService.getTopSyntaxEnum(TypeEnum.NOUN));
        model.addAttribute("topAdverb", wordService.getTopSyntaxEnum(TypeEnum.ADVERB));
        model.addAttribute("topAdjective", wordService.getTopSyntaxEnum(TypeEnum.ADJECTIVE));
        model.addAttribute("topEmoji", wordService.getTopSyntaxEnum(TypeEnum.EMOJI));
        model.addAttribute("topKicheWords", wordService.getTop10WordsBySyntax(TypeEnum.KITSCH));
        model.addAttribute("topGrotesqueWords", wordService.getTop10WordsBySyntax(TypeEnum.GROTESQUE));
        model.addAttribute("top20Words", wordService.getTop20Words());
        return "thesaurus/index";
    }


    @GetMapping("/fragment/words_table_tweets")
    public String getResultBySearchKey(Model model, @RequestParam("word") Optional<String> word) {
        {
            if (word.isPresent()) {
                System.out.println(word.get());
                model.addAttribute("tweets", tweetService.findByText(word.get()));
                model.addAttribute("replies", repliesService.findAllByTextContaining(word.get()));
            }else{
                model.addAttribute("tweets", "yes");
            }

            return "thesaurus/fragment/words_table_tweets :: words_table_tweets";
        }
    }

    @GetMapping("/fragment/table_title")
    public String getTableTile(Model model, @RequestParam("word") Optional<String> word) {
        {
            if (word.isPresent()) {
                model.addAttribute("title", "Tweets containing " + word.get());
            }else{
                model.addAttribute("title", "Search for a word");
            }

            return "thesaurus/fragment/table_title :: wordTitle";
        }
    }
}
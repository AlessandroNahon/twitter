package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.services.implementations.RepliesServiceImpl;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/thesaurus")
public class ThesaurusController {

    @Autowired
    private WordService wordService;

    @Autowired
    private RepliesService repliesService;

    @Autowired
    private TweetService tweetService;



    public ThesaurusController(){
    }

    /**
     * This method initializes the index page of the fragment, passing by the model all the information about tweets,
     * types of words and count of those words.
     * @param model The model where we are going to extract the information into the html
     * @return The path to the thesaurus index file
     */
    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("topNoun", wordService.getTopSyntaxEnum(TypeEnum.NOUN));
        model.addAttribute("topAdverb", wordService.getTopSyntaxEnum(TypeEnum.ADVERB));
        model.addAttribute("topAdjective", wordService.getTopSyntaxEnum(TypeEnum.ADJECTIVE));
        model.addAttribute("topEmoji", getTopEmoji());
        model.addAttribute("topKicheWords", wordService.getTop10WordsBySyntax(TypeEnum.KITSCH));
        model.addAttribute("topGrotesqueWords", wordService.getTop10WordsBySyntax(TypeEnum.GROTESQUE));
        model.addAttribute("top20Words", wordService.getTop20Words());
        return "thesaurus/index";
    }

    public Word getTopEmoji(){
        Word grotesque = wordService.getTopSyntaxEnum(TypeEnum.GROTESQUE_EMOJI);
        Word kisch = wordService.getTopSyntaxEnum(TypeEnum.KITSCH_EMOJI);
        if(grotesque == null){
            return kisch;
        }else if(kisch == null){
            return grotesque;
        }
        if(grotesque.getCount() > kisch.getCount()){
            return grotesque;
        }else{
            return kisch;
        }
    }

    /**
     * This method is used by the Thesaurus website in order to inject a fragment with a table with all the tweets/
     * replies that contains a word passed by parameter
     * @param model The model where we are going to extract the information into the html
     * @param word The word that we're going to find into the database
     * @return The path of the file that contains the fragment and the name of the fragment itself
     */
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

    /**
     * This method is used by the Thesaurus website in order to inject a fragment with the word that is going
     * to be searched
     * @param model The model where we are going to extract the information into the html
     * @param word The word that we're going to find into the database
     * @return The path of the file that contains the fragment and the name of the fragment itself
     */
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
package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
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
        String searchType = WordServiceImpl.TWEET_BELONGS_TO;
        model.addAttribute("topNoun", wordService.getTopByBelongsToBySyntax(searchType,TypeEnum.NOUN));
        model.addAttribute("topAdverb", wordService.getTopByBelongsToBySyntax(searchType,TypeEnum.ADVERB));
        model.addAttribute("topAdjective", wordService.getTopByBelongsToBySyntax(searchType,TypeEnum.ADJECTIVE));
        model.addAttribute("topEmoji", getTopEmoji(searchType));
        model.addAttribute("topKicheWords", wordService.getTop10ByBelongsToBySyntax(searchType,TypeEnum.KITSCH));
        model.addAttribute("topGrotesqueWords", wordService.getTop10ByBelongsToBySyntax(searchType,TypeEnum.GROTESQUE));
        model.addAttribute("top20Words", wordService.getTop20ByBelongsTo(searchType));
        return "thesaurus/index";
    }

    public Word getTopEmoji(String searchType) {
        Word grotesque = wordService.getTopByBelongsToBySyntax(searchType, TypeEnum.GROTESQUE_EMOJI);
        Word kisch = wordService.getTopByBelongsToBySyntax(searchType, TypeEnum.KITSCH_EMOJI);
            if (grotesque == null && kisch == null) {
                return new Word();
            } else if (kisch == null) {
                return grotesque;
            } else if(grotesque == null) {
                return kisch;
            }else{
                if (grotesque.getCount() > kisch.getCount()) {
                    return grotesque;
                } else {
                    return kisch;
                }
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
            if (word.isPresent()) {
                model.addAttribute("tweets", tweetService.findByText(word.get()));
                model.addAttribute("replies", repliesService.findAllByTextContaining(word.get()));
            }else{
                model.addAttribute("tweets", "not present");
            }
            return "thesaurus/fragment/words_table_tweets :: words_table_tweets";
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
            if (word.isPresent()) {
                model.addAttribute("title", "Tweets containing " + word.get());
            }else{
                model.addAttribute("title", "Search for a word");
            }
            return "thesaurus/fragment/table_title :: wordTitle";
    }


    @GetMapping("/fragment/whole_display_info")
    public String getFullThesaurusBasedOnType(Model model, @RequestParam("type") String type){
        System.out.println("\t\n\tTYPE TIENE COMO VALOR: "+type+"\n\n");
        model.addAttribute("topNoun", wordService.getTopByBelongsToBySyntax(type,TypeEnum.NOUN));
        model.addAttribute("topAdverb", wordService.getTopByBelongsToBySyntax(type,TypeEnum.ADVERB));
        model.addAttribute("topAdjective", wordService.getTopByBelongsToBySyntax(type,TypeEnum.ADJECTIVE));
        model.addAttribute("topEmoji", getTopEmoji(type));
        model.addAttribute("topKicheWords", wordService.getTop10ByBelongsToBySyntax(type,TypeEnum.KITSCH));
        model.addAttribute("topGrotesqueWords", wordService.getTop10ByBelongsToBySyntax(type,TypeEnum.GROTESQUE));
        model.addAttribute("top20Words", wordService.getTop20ByBelongsTo(type));
        return "thesaurus/fragment/whole_display_info :: whole_display_info";
    }
}
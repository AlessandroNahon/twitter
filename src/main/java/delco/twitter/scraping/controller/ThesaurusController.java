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

    @RequestMapping("/index")
    public String index() {

        return "thesaurus/index";
    }

    @GetMapping("/fragment/whole_display_info")
    public String getWholeFragment(Model model, @RequestParam("organization") String organization,
                                   @RequestParam("belongsTo") String belongsTo,@RequestParam("lookingFor") String syntax) {
        model.addAttribute("topNoun", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo,TypeEnum.NOUN,organization));
        model.addAttribute("topAdverb", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo,TypeEnum.ADVERB,organization));
        model.addAttribute("topAdjective", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo,TypeEnum.ADJECTIVE,organization));
        model.addAttribute("topEmoji", wordService.getTopEmojiByBelongsAndOrganization(belongsTo,organization));
        model.addAttribute("topNoun", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo,TypeEnum.NOUN,organization));
        model.addAttribute("top20Words", wordService.getSortedByBelongsAndOrganization(belongsTo,organization,20));
        model.addAttribute("topKicheWords",wordService.getSortedByBelongsOrganizationAndSyntax(belongsTo,organization,TypeEnum.KITSCH,10));
        model.addAttribute("topGrotesqueWords",wordService.getSortedByBelongsOrganizationAndSyntax(belongsTo,organization,TypeEnum.GROTESQUE,10));
        return "thesaurus/fragment/whole_display_info :: whole_display_info";
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



//
//    @GetMapping("/fragment/special_words")
//    public String getThesaurusKischTable(Model model, @RequestParam("classification") String classification,
//                                         @RequestParam("belongsTo") String belongsTo){
//        TypeEnum firstType = classification.equals("Words") ? TypeEnum.KITSCH : TypeEnum.KITSCH_EMOJI;
//        TypeEnum secondType = (firstType == TypeEnum.KITSCH) ? TypeEnum.GROTESQUE : TypeEnum.GROTESQUE_EMOJI;
//        model.addAttribute("kischWords", wordService.getTop10ByBelongsToBySyntax(belongsTo,firstType));
//        model.addAttribute("grotesqueWords", wordService.getTop10ByBelongsToBySyntax(belongsTo,secondType));
//        return "thesaurus/fragment/special_words :: special_words";
//    }
}
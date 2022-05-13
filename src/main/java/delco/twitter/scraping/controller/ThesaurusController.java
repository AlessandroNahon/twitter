package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
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


    public ThesaurusController() {
    }

    @RequestMapping("/index")
    public String index() {

        return "thesaurus/index";
    }

    @GetMapping("/fragment/whole_display_info")
    public String getWholeFragment(Model model, @RequestParam("organization") String organization,
                                   @RequestParam("belongsTo") String belongsTo, @RequestParam("lookingFor") String syntax) {
        model.addAttribute("topNoun", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo, TypeEnum.NOUN, organization));
        model.addAttribute("topAdverb", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo, TypeEnum.ADVERB, organization));
        model.addAttribute("topAdjective", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo, TypeEnum.ADJECTIVE, organization));
        model.addAttribute("topEmoji", wordService.getTopEmojiByBelongsAndOrganization(belongsTo, organization));
        model.addAttribute("topNoun", wordService.getTopByBelongsSyntaxAndOrganization(belongsTo, TypeEnum.NOUN, organization));
        model.addAttribute("top20Words", wordService.getSortedByBelongsAndOrganization(belongsTo, organization,true, 20));
        model.addAttribute("topKicheWords", wordService.getSortedByBelongsOrganizationAndSyntax(belongsTo, organization, TypeEnum.KITSCH, 10));
        model.addAttribute("topGrotesqueWords", wordService.getSortedByBelongsOrganizationAndSyntax(belongsTo, organization, TypeEnum.GROTESQUE, 10));
        return "thesaurus/fragment/whole_display_info :: whole_display_info";
    }

    @GetMapping("/fragment/special_words")
    public String getSpecialWords(Model model, @RequestParam("organization") String organization,
                                  @RequestParam("belongsTo") String belongsTo, @RequestParam("lookingFor") String syntax) {
        TypeEnum positiveType = syntax.equals("Word") ? TypeEnum.KITSCH : TypeEnum.KITSCH_EMOJI;
        TypeEnum negativeType = positiveType.equals(TypeEnum.KITSCH) ? TypeEnum.GROTESQUE : TypeEnum.GROTESQUE_EMOJI;
        model.addAttribute("topKicheWords", wordService.getSortedByBelongsOrganizationAndSyntax(belongsTo, organization, positiveType, 10));
        model.addAttribute("topGrotesqueWords", wordService.getSortedByBelongsOrganizationAndSyntax(belongsTo, organization, negativeType, 10));
        return "thesaurus/fragment/special_words :: special_words";
    }


    /**
     * This method is used to return the
     *
     * @param model
     * @param word
     * @param organization
     * @param belongsTo
     * @return
     */
    @GetMapping("/fragment/words_table_tweets")
    public String getTableTile(Model model, @RequestParam("word") String word,
                               @RequestParam("organization") String organization,
                               @RequestParam("belongsTo") String belongsTo,
                               @RequestParam("page") int page) {
        int maxPages = 0;
        int currentPage = page-1;
        if (belongsTo.equals("Tweet")) {
            List<Tweet> tweetList = tweetService.findByText(word, organization);
            model.addAttribute("tweets", tweetList.subList(currentPage * 10,
                    Math.min(currentPage * 10 + 10, tweetList.size())));
            maxPages = tweetList.size() % 10 == 0? tweetList.size() / 10 : tweetList.size() / 10 + 1;
        } else {
            List<Reply> repliesList = repliesService.findByText(word, organization);
            model.addAttribute("replies", repliesList.subList(currentPage * 10,
                    Math.min(currentPage * 10 + 10, repliesList.size())));
            maxPages = repliesList.size() % 10 == 0? repliesList.size() / 10 : repliesList.size() / 10 + 1;
        }
        model.addAttribute("maxPages", maxPages);
        return "thesaurus/fragment/words_table_tweets :: words_table_tweets";
    }


    @GetMapping("/fragment/word_cloud")
    public String getWordCloud(Model model, @RequestParam("organization") String organization,
                               @RequestParam("belongsTo") String belongsTo) {
        model.addAttribute("wordList", wordService
                .getSortedByBelongsAndOrganization(belongsTo, organization, false, 100));
        return "thesaurus/fragment/word_cloud :: word_cloud";
    }

}




package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.enumerations.SyntaxEnum;
import delco.twitter.scraping.services.implementations.WordServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/thesaurus")
public class ThesaurusController {

    private final WordServiceImpl wordService;

    public ThesaurusController(WordServiceImpl wordService) {
        this.wordService = wordService;
    }

    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("topNoun", wordService.getTopSyntaxEnum(SyntaxEnum.NOUN));
        model.addAttribute("topAdverb", wordService.getTopSyntaxEnum(SyntaxEnum.ADVERB));
        model.addAttribute("topAdjective", wordService.getTopSyntaxEnum(SyntaxEnum.ADJECTIVE));
        model.addAttribute("topEmoji", wordService.getTopSyntaxEnum(SyntaxEnum.EMOJI));
        model.addAttribute("topKicheWords", wordService.getTop10WordsBySyntax(SyntaxEnum.KITSCH));
        model.addAttribute("topGrotesqueWords", wordService.getTop10WordsBySyntax(SyntaxEnum.GROTESQUE));
        model.addAttribute("top20Words", wordService.getTop20Words());
        return "thesaurus/index";
    }

}
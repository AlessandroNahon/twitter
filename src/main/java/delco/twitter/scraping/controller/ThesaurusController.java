package delco.twitter.scraping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/thesaurus")
public class ThesaurusController {

    @RequestMapping("/index")
    public String index() {
        return "thesaurus/index";
    }

}
package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/tweet")
public class SearchTweetController {

    @Autowired
    private TweetService tweetService;

    @RequestMapping("/searchIndex")
    public String showSearchIndex(){
        return "tweet/searchIndex";
    }

    @RequestMapping("/fragments/table_search_tweet")
    public String getSearch(Model model, @RequestParam("searchBy") String searchType,
                            @RequestParam("searchValue") String searchValue,
                            @RequestParam("organization") String organization,
                            @RequestParam("currentPage") int pageNumber){
        int maxPages = 0;
        int currentPage = pageNumber-1;
        List<Tweet> tweetList = new ArrayList<>();
        if(searchType.equals("username")){
            tweetList = tweetService.findByUsername(searchValue);
        }else if(searchType.equals("sentiment")){
            tweetList = tweetService.findBySentiment(searchValue);
        }else if(searchType.equals("image")) {
            tweetList = tweetService.findByImageContent(searchValue);
        }else{
            tweetList = tweetService.findByText(searchValue, organization);
        }
        maxPages = tweetList.size() % 10 == 0? tweetList.size() / 10 : tweetList.size() / 10 + 1;
        model.addAttribute("tweetList",tweetList.subList(currentPage*10,Math.min(currentPage * 10 + 10, tweetList.size())));
        model.addAttribute("maxPages",maxPages);
        return "tweet/fragments/table_search_tweet :: table_search_tweet";
    }


}

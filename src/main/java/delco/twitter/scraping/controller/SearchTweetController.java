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
                            @RequestParam("pageNumber") int pageNumber){
        List<Tweet> tweetList = new ArrayList<>();
        pageNumber--;
        if(searchType.equals("username")){
            tweetList = getPagination(tweetService.findByUsername(searchValue), pageNumber,model);
        }else if(searchType.equals("sentiment")){
            tweetList = getPagination(tweetService.findBySentiment(searchValue),pageNumber,model);
        }else if(searchType.equals("image")) {
            tweetList = getPagination(tweetService.findByImageContent(searchValue),pageNumber,model);
        }else{
            tweetList = getPagination(tweetService.findByText(searchValue),pageNumber,model);
        }
        model.addAttribute("tweetList",tweetList);
        return "tweet/fragments/table_search_tweet :: table_search_tweet";
    }

    public List<Tweet> getPagination(List<Tweet> listTweet, int pageNumber, Model model){
        if(listTweet.size()>10) {
            int maxPages = listTweet.size()%10 == 0 ? (listTweet.size()/10) : ((listTweet.size()/10)+1);
            model.addAttribute("maxPages",maxPages);
            return listTweet.subList(pageNumber * 10, Math.min(pageNumber * 10 + 10, listTweet.size()));
        }else{
            model.addAttribute("maxPages","1");
            return listTweet.subList(0,Math.min(10,listTweet.size()));
        }
    }


}

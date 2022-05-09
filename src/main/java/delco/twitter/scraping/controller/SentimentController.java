package delco.twitter.scraping.controller;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.services.interfaces.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sentiment")
public class SentimentController {

    @Autowired
    private SentimentService sentimentService;


    @RequestMapping("/index")
    public String index(Model model) {
        List<Integer> list = sentimentService.analyzeDatabaseByTypeAndClassification("Sentimental");
        model.addAttribute("sentimentAndImages",list.get(0));
        model.addAttribute("sentimentAndEmojis",list.get(1));
        model.addAttribute("fullSearch",list.get(2));
        return "sentiment/index";
    }


    @RequestMapping("/fragments/full_fragment")
    public String fullFragment(Model model, @RequestParam String type, @RequestParam String classification) {
        if(!type.equals("Grey")) {
            List<Integer> list = sentimentService.analyzeDatabaseByTypeAndClassification(classification);
            model.addAttribute("sentimentAndImages", list.get(0));
            model.addAttribute("sentimentAndEmojis", list.get(1));
            model.addAttribute("fullSearch", list.get(2));
        }else{
            model.addAttribute("listOfTweets",getPagination(sentimentService.findAllOtherTweets(),1, model));
            return "sentiment/fragments/tweet_table :: tweet_table";
        }
        return "sentiment/fragments/full_fragment :: layers_card";
    }

    /**
     * This method is used by the three chart cards
     * @param model Object to pass the information to Thymeleaf
     * @param searchType Indicates whether the SentimentService has to search for matches of
     *                   Sentiment & Images, Sentiment & Emojis or Full Search
     * @param classification Indicates to the Sentiment Service if it has to search for the matches of the
     *                    Kistch tweets or the Grotesque tweets
     * @param belongsTo Indicates to the Sentiment Service if, from the list of matches, gather the tweets or the
     *                  replies
     * @return The path to the Fragment (Table)
     */
    @RequestMapping("/fragments/tweet_table")
    public String getTableWithTweets(Model model,@RequestParam("searchType") String searchType, @RequestParam("classification")
                                     String classification, @RequestParam("belongsTo") String belongsTo,
                                     @RequestParam("page") Optional<String> page) {
        int pageNumber = page.isPresent() ? Integer.parseInt(page.get())-1 : 0;
        if(classification.equals("Sentimental") || classification.equals("Disruptive")){
            List<Integer> list = sentimentService.analyzeDatabaseByTypeAndClassification(classification);
            model.addAttribute("sentimentAndImages", list.get(0));
            model.addAttribute("sentimentAndEmojis", list.get(1));
            model.addAttribute("fullSearch", list.get(2));
            if(belongsTo.equals("Tweet")){
                model.addAttribute("listOfTweets",getPagination(
                        sentimentService.getTweetsBySearchAndLookingFor(classification,searchType),pageNumber, model));
            }else{
                model.addAttribute("listOfReply",getPaginationReply(
                        sentimentService.getRepliesBySearchAndLookingFor(classification,searchType),pageNumber, model));
            }
            return "sentiment/fragments/full_fragment :: layers_card";
        }else{
            if(belongsTo.equals("Tweet")){
                model.addAttribute("listOfTweets",getPagination(sentimentService.findAllOtherTweets(),pageNumber, model));
            }else{
                model.addAttribute("listOfReply",getPaginationReply(sentimentService.findAllOtherReply(),pageNumber,model));
            }
        }
        return "sentiment/fragments/tweet_table :: tweet_table";
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

    public List<Reply> getPaginationReply(List<Reply> listReply, int pageNumber, Model model){
        if(listReply.size()>10) {
            int maxPages = listReply.size()%10 == 0 ? (listReply.size()/10) : ((listReply.size()/10)+1);
            model.addAttribute("maxPages",maxPages);
            return listReply.subList(pageNumber * 10, Math.min(pageNumber * 10 + 10, listReply.size()));
        }else{
            model.addAttribute("maxPages","1");
            return listReply.subList(0,Math.min(10,listReply.size()));
        }
    }




}

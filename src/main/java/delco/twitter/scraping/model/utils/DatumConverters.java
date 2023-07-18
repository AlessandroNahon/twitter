package delco.twitter.scraping.model.utils;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import delco.twitter.scraping.model.twitterapi.model_content.Datum;
import delco.twitter.scraping.services.interfaces.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatumConverters {

    @Autowired
    private SentimentService sentimentService;

    public Tweet convertDatumToTweet(Datum datum, String username) {
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(datum.getCreated_at());
        tweet.setPossibly_sensitive(datum.isPossibly_sensitive());
        tweet.setText(datum.getText()
                .replace("&gt;&gt;", "")
                .replace("&gt+", "")
                .replace("&gt;", ""));
        tweet.setConversationId(datum.getConversation_id());
        try{
            tweet.setTextSentiment(sentimentService.getSentiment(clearTextFromTrash(datum.getText()),username,"Tweet"));
        }catch (Exception e){
            System.out.println("Error: "+e.getMessage());
            tweet.setTextSentiment(SentimentEnum.NEUTRAL);
        }

        return tweet;
    }

    public Reply convertDatumToReply(Datum d, String originalUsername){
        Reply reply = new Reply();
        reply.setText(d.getText()
                .replace("&gt;&gt;", "")
                .replace("&gt+", "")
                .replace("&gt;", ""));
        try{
            reply.setTextSentiment(sentimentService.getSentiment(clearTextFromTrash(d.getText()),originalUsername,"Reply"));
        }catch (Exception e){
            reply.setTextSentiment(SentimentEnum.NEUTRAL);
        }

        reply.setOrganization(originalUsername);
        return reply;
    }

    private String clearTextFromTrash(String text){
        text = text.replaceAll("http.*?\\s"," ").replaceAll("@.*?\\s"," ")
                   .replaceAll("#[A-Za-z]+","");
        return text.substring(0, (text.contains("https://") ? text.indexOf("https://") : text.length())).trim();
    }


}

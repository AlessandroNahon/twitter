package delco.twitter.scraping.model.utils;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Datum;
import delco.twitter.scraping.services.interfaces.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatumConverters {

    @Autowired
    private SentimentService sentimentService;

    public Tweet convertDatumToTweet(Datum datum) {
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(datum.getCreated_at());
        tweet.setPossibly_sensitive(datum.isPossibly_sensitive());
        tweet.setText(datum.getText()
                .replace("&gt;&gt;", "")
                .replace("&gt+", "")
                .replace("&gt;", ""));
        tweet.setConversationId(datum.getConversation_id());
        tweet.setTextSentiment(sentimentService.getSentiment(datum.getText()));
        return tweet;
    }

    public Reply convertDatumToReply(Datum d){
        Reply reply = new Reply();
        reply.setText(d.getText()
                .replace("&gt;&gt;", "")
                .replace("&gt+", "")
                .replace("&gt;", ""));
        reply.setTextSentiment(sentimentService.getSentiment(d.getText()));
        return reply;
    }


}

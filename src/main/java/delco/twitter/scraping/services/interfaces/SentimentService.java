package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Sentiments;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.enumerations.SentimentEnum;

import java.util.List;

public interface SentimentService {

    SentimentEnum getSentiment(String text, String originalUsername, String belongsTo);

    void addAppearance(SentimentEnum sentiment, String originalUsername, String belongsTo);

    List<Sentiments> getSentimentsByOrganizationAndBelongs(String originalUsername, String belongsTo);

    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

}

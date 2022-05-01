package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Sentiment;
import delco.twitter.scraping.model.enumerations.SentimentEnum;

import java.util.List;

public interface SentimentService {

    SentimentEnum getSentiment(String text);

    void addAppearance(Long id);

    /*
    Methods that access directly to the repository, they do not contain bussiness logic, only works as intermediates
    between the view and the model
     */

    Iterable<Sentiment> findAllSentiment();

}

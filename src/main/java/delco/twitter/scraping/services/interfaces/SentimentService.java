package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Sentiment;
import delco.twitter.scraping.model.enumerations.SentimentEnum;

import java.util.List;

public interface SentimentService {

    SentimentEnum getSentiment(String text);

    void addAppearance(Long id);

    Iterable<Sentiment> findAllSentiment();

}

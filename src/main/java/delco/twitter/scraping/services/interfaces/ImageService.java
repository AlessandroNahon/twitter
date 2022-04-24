package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;

public interface ImageService {

    void setImages(Includes include, Datum datum, Tweet originalTweet);

    void setImages(Includes include, Datum datum, Reply originalTweet);

}

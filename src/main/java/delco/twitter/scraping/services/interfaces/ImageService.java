package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;

import java.net.URL;
import java.util.List;
import java.util.Set;

public interface ImageService {

    void getImages(Includes include, Datum datum, Tweet originalTweet);

    void getImages(Includes include, Datum datum, Reply originalTweet);

    Images downloadImage(URL url);

    boolean getImageContent(String url);

    boolean detectAdult(String url);

}

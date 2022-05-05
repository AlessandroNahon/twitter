package delco.twitter.scraping.services.interfaces;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.twitterapi.model_content.Datum;
import delco.twitter.scraping.model.twitterapi.model_content.Includes;

import java.net.URL;
import java.util.List;

public interface ImageService {

    void addLabelsAndSaveImage(Images image);

    List<Images> getImages(Includes include, Datum datum);

    Images downloadImage(String url,boolean tweetHasSensibleContent);

    void annotateImageWitObjects(List<String> responses, Images image);

    void changeImageClassification(String id, String actualClassification);


}

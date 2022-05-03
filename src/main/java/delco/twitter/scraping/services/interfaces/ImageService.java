package delco.twitter.scraping.services.interfaces;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.twitterapi.model_content.Datum;
import delco.twitter.scraping.model.twitterapi.model_content.Includes;

import java.net.URL;
import java.util.List;

public interface ImageService {

    void saveImageWithTweet(Images image);

    List<Images> getImages(Includes include, Datum datum);

    Images downloadImage(String url);

    Images downloadImagesWithoutAnalysis(String url);

    List<Images> getImagesWithoutAnalysis(Includes include, Datum datum);

    void annotateImageWithObjects(List<AnnotateImageResponse> responses, Images image);


}

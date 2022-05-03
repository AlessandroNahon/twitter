package delco.twitter.scraping.services.interfaces;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import delco.twitter.scraping.model.enumerations.TypeEnum;

import java.util.List;

public interface VisionAPIService {

    List<AnnotateImageResponse> getValidPictureType(String path);

    List<AnnotateImageResponse> getContentOfThePicture(String path);

    Boolean getSafeSearch(String path);

}

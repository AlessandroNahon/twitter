package delco.twitter.scraping.services.interfaces;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Image;
import delco.twitter.scraping.model.enumerations.TypeEnum;

import java.util.List;

public interface VisionAPIService {

    List<String> getValidPictureType(String path);

    List<String> detectLabels(Image img);

    Boolean getSafeSearch(String path);

}

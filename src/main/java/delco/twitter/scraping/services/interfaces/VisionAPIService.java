package delco.twitter.scraping.services.interfaces;

import java.util.List;

public interface VisionAPIService {

    List<String> getValidPictureType(String path);

    List<String> detectLabels(String url);

    Boolean getSafeSearch(String path);

}

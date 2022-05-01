package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.enumerations.TypeEnum;

public interface VisionAPIService {

    Boolean getPictureType(String path);

    Boolean getSafeSearch(String path);

}

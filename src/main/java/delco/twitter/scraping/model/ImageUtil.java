package delco.twitter.scraping.model;

import java.util.Base64;

public class ImageUtil {

    public String getImgData(byte[] byteData) {
        return Base64.getMimeEncoder().encodeToString(byteData);
    }

}

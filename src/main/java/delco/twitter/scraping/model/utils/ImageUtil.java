package delco.twitter.scraping.model.utils;

import java.util.Base64;

public class ImageUtil {

    /**
     * This method is used by the thymeleaf template to parse all the images object (Which are stored into the
     * database as blob, and in java as byte[]) into an actual image, and then show it in the website
     * @param byteData The image that is going to be parsed
     * @return The image enconded as a String object
     */
    public String getImgData(byte[] byteData) {
        return Base64.getMimeEncoder().encodeToString(byteData);
    }

}

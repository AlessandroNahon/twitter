package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Medium;
import delco.twitter.scraping.services.interfaces.ImageService;
import delco.twitter.scraping.services.interfaces.VisionAPIService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private VisionAPIService visionAPIService;

    @SneakyThrows
    public ImageServiceImpl(){}

    /**
     * This method is used to recover the images from internet and save them in the database
     * @param include The object includes, from root, where you find the URL of the Tweet image
     * @param datum The object datum, from root, where you find the media keys of the tweet. The program
     *              search for the coincidence between the media keys and the media keys of the tweet and
     *              assign the image to the tweet.
     */
    @Override
    public List<Images> getImages(Includes include, Datum datum) {
        ArrayList<Images> images = new ArrayList<>();
        BufferedImage image = null;
        try{
            for(String mediaKey : datum.getAttachments().getMedia_keys()){
                for(Medium media : include.getMedia()){
                    if(media.getMedia_key().equals(mediaKey)){
                        try {
                            URL url = media.getType().equals("photo") ? new URL(media.getUrl())
                                    : new URL(media.getPreview_image_url());
                            images.add(downloadImage(url));
                        }  catch (IOException | NullPointerException e) {
                            System.out.println("ERROR EN LA IMAGEN "+e.getMessage());
                        }

                    }
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return images;
    }



    /**
     * This method is used to download an image from the internet. In this program, all the paths of the different
     * images comes from the Tweet element, so this method converts a ByteArrayOutputStream into an Images object
     * @param url The url to the image from twitter
     * @return Objects images to be assigned to the tweet
     */
    @Override
    public Images downloadImage(URL url) {
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(ImageIO.read(url), "jpg", baos);
            Images i = new Images();
            i.setImage(baos.toByteArray());
            if(visionAPIService.getSafeSearch(url.toString())){
                i.setImageContent(TypeEnum.GROTESQUE);
            }else{
                i.setImageContent(TypeEnum.KITSCH);
            }
            return i;
        }  catch (IOException | NullPointerException e) {
            System.out.println("ERROR EN LA IMAGEN "+e.getMessage());
        }
        return null;
    }

    @Override
    public boolean containsValidImages(Includes include, Datum datum) {
        System.out.println("Entra a analizar las imágenes");
        for (String mediaKey : datum.getAttachments().getMedia_keys()) {
            for (Medium media : include.getMedia()) {
                if (media.getMedia_key().equals(mediaKey)) {
                    String url = media.getType().equals("photo") ? media.getUrl()
                            : media.getPreview_image_url();
                    if (visionAPIService.getPictureType(url)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}


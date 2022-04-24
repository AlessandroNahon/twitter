package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Image;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Medium;
import delco.twitter.scraping.services.interfaces.ImageService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public void setImages(Includes include, Datum datum, Tweet originalTweet) {
        BufferedImage image = null;
        try{
            for(String mediaKey : datum.getAttachments().getMedia_keys()){
                for(Medium media : include.getMedia()){
                    if(media.getMedia_key().equals(mediaKey)){
                        URL url = null;
                        try {
                            if(media.getType().equals("photo")){
                                url = new URL(media.getUrl());
                            }else{
                                url = new URL(media.getPreview_image_url());
                            }
                            image = ImageIO.read(url);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(image, "jpg", baos);
                            Image i = new Image();
                            i.setImage(baos.toByteArray());
                            originalTweet.addImage(i);
                            System.out.println("\tIMAGEN AÑADIDA CORRECTAMENTE");
                        }  catch (IOException | NullPointerException e) {
                            System.out.println("ERROR EN LA IMAGEN "+e.getMessage());
                        }

                    }
                }
            }
        }catch (NullPointerException e){
            System.out.println("No media for Tweet with ID: "+originalTweet.getId());
        }
    }

    @Override
    public void setImages(Includes include, Datum datum, Reply originalTweet) {
        BufferedImage image = null;
        try{
            for(String mediaKey : datum.getAttachments().getMedia_keys()){
                for(Medium media : include.getMedia()){
                    if(media.getMedia_key().equals(mediaKey)){
                        URL url = null;
                        try {
                            if(media.getType().equals("photo")){
                                url = new URL(media.getUrl());
                            }else{
                                url = new URL(media.getPreview_image_url());
                            }
                            image = ImageIO.read(url);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(image, "jpg", baos);
                            Image i = new Image();
                            i.setImage(baos.toByteArray());
                            originalTweet.addImage(i);
                        }  catch (IOException | NullPointerException e) {
                            System.out.println("ERROR EN LA IMAGEN "+e.getMessage());
                        }

                    }
                }
            }
        }catch (NullPointerException e){
            System.out.println("No media for Tweet with ID: "+originalTweet.getId());
        }
    }
}

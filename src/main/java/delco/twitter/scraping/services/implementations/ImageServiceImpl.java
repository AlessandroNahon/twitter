package delco.twitter.scraping.services.implementations;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Medium;
import delco.twitter.scraping.services.interfaces.ImageService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {

    private TreeSet<String> acceptedImages;
    private TreeSet<String> notAcceptedImages;
    private final ImageAnnotatorClient client;
    private final String LIKELY = "LIKELY";
    private final String POSSIBLE = "POSSIBLE";
    private final String VERY_LIKELY = "VERY_LIKELY";

    @SneakyThrows
    public ImageServiceImpl(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                acceptedImages = readFiles("accepted");
                notAcceptedImages = readFiles("not_accepted");
            }
        }).start();
        client = ImageAnnotatorClient.create();
    }

    public TreeSet<String> readFiles(String fileName) {
        try {
            File fichero =  ResourceUtils.getFile("classpath:"+fileName+".dat");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero));
            return (TreeSet<String>) ois.readObject();
        } catch (IOException e) {
            System.out.println("The file was not found");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("The file was not found");
    }


    /**
     * This method is used to recover the images from internet and save them in the database
     * @param include The object includes, from root, where you find the URL of the Tweet image
     * @param datum The object datum, from root, where you find the media keys of the tweet. The program
     *              search for the coincidence between the media keys and the media keys of the tweet and
     *              assign the image to the tweet.
     * @param originalTweet The tweet where you want to assign the image
     */
    @Override
    public void getImages(Includes include, Datum datum, Tweet originalTweet) {
        BufferedImage image = null;
        try{
            for(String mediaKey : datum.getAttachments().getMedia_keys()){
                for(Medium media : include.getMedia()){
                    if(media.getMedia_key().equals(mediaKey)){
                        try {
                            URL url = media.getType().equals("photo") ? new URL(media.getUrl())
                                    : new URL(media.getPreview_image_url());
                            originalTweet.addImage(downloadImage(url));
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

    /**
     * Overload of the previous method, but using replies instead of tweets
     * @param include The object includes, from root, where you find the URL of the Tweet image
     * @param datum The object datum, from root, where you find the media keys of the tweet. The program
     *              search for the coincidence between the media keys and the media keys of the tweet and
     *              assign the image to the tweet.
     * @param originalTweet The tweet where you want to assign the image
     */
    @Override
    public void getImages(Includes include, Datum datum, Reply originalTweet) {
        BufferedImage image = null;
        try{
            for(String mediaKey : datum.getAttachments().getMedia_keys()){
                for(Medium media : include.getMedia()){
                    if(media.getMedia_key().equals(mediaKey)){
                        try {
                            URL url = media.getType().equals("photo") ? new URL(media.getUrl())
                                    : new URL(media.getPreview_image_url());
                            originalTweet.addImage(downloadImage(url));
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
    public Images downloadImage(URL url) {
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(ImageIO.read(url), "jpg", baos);
            Images i = new Images();
            i.setImage(baos.toByteArray());
            return i;
        }  catch (IOException | NullPointerException e) {
            System.out.println("ERROR EN LA IMAGEN "+e.getMessage());
        }
        return null;
    }

    @Override
    public boolean getImageContent(String url) {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
//        requests.add(
//                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(
//                        Image.newBuilder().setSource(
//                                ImageSource.newBuilder().setImageUri(url).build()).build()).build());
        Image img = Image.newBuilder().setSource
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();
        int counterPositive = 0;
        int counterNegative = 0;
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.format("Error: %s%n", res.getError().getMessage());
            }else{
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    System.out.println(annotation.getDescription());
                    if(acceptedImages.contains(annotation.getDescription())){
                        counterPositive++;
                        System.out.println("\t ACEPTADA: "+annotation.getDescription());
                    }else if(notAcceptedImages.contains(annotation.getDescription())){
                        counterNegative++;
                        System.out.println("\t RECHAZADA: "+annotation.getDescription());
                    }
                }
            }
        }
        return counterPositive-1 >= counterNegative;
    }


    @Override
    public boolean detectAdult(String url) {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Feature feat = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();


        /* Here is where the Request is created. It creates an ImageSource from the internet, and then set that
        Image source to the object Image, and lastly that image is added as a new Request on the ArrayList */
        requests.add(
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(
                Image.newBuilder().setSource(
                ImageSource.newBuilder().setImageUri(url).build()).build()).build());

        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        for (AnnotateImageResponse res : response.getResponsesList()) {
            if (res.hasError()) {
                System.out.format("Error: %s%n", res.getError().getMessage());
                return false;
            }
            SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();

            //Here we convert all the received fields an convert it to a List in order to Iterate over it
            for(Map.Entry e : annotation.getAllFields().entrySet().stream().collect(Collectors.toList())) {

                /*
                The program compares if the Key of the Entry is the same as the Key of the field Spoof, returning
                zero (0) if those two matches. In that case, we're not going to analyze the rest because we're not
                looking to detect if the image is a Spoof.
                 */
                if(((Descriptors.FieldDescriptor) e.getKey())
                        .compareTo(SafeSearchAnnotation.getDescriptor().findFieldByName("spoof")) != 0){

                    /*
                    If the values of each entry are similar to one of these three constants, we're assuming the image
                    is "Grotesque"
                     */
                    if(e.getValue().toString().equals(LIKELY) ||
                            e.getValue().toString().equals(POSSIBLE)  ||
                            e.getValue().toString().equals(VERY_LIKELY) ){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void shutDownClient(){
        client.close();
    }
}


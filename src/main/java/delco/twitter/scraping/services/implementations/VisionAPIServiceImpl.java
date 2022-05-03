package delco.twitter.scraping.services.implementations;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import delco.twitter.scraping.services.interfaces.VisionAPIService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.util.*;

@Service
public class VisionAPIServiceImpl extends Thread implements VisionAPIService {

    private TreeSet<String> acceptedImages;
    private TreeSet<String> notAcceptedImages;
    boolean areWordsLoaded = false;
    private final ImageAnnotatorClient client;



    @SneakyThrows
    public VisionAPIServiceImpl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                acceptedImages = readFiles("accepted");
                notAcceptedImages = readFiles("not_accepted");
                areWordsLoaded = true;
            }
        }).start();
        client = ImageAnnotatorClient.create();
    }

    /**
     * This methdod is used to load the .dat files from the resources folder, which contains all the labels that
     * are accepted or not in order to determine wheter an image contains animals and/or nature
     * @param fileName The name of the file, without the .dat
     * @return A tree set that is going to be assigned to the class variable acceptedImages/notAcceptedImages
     */
    public TreeSet<String> readFiles(String fileName) {
        try {
            File fichero =  ResourceUtils.getFile("classpath:wordsList"+File.separator+fileName+".dat");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero));
            return (TreeSet<String>) ois.readObject();
        } catch (IOException e) {
            System.out.println("The file was not found");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("The file was not found");
    }

    @Override
    public List<AnnotateImageResponse> getValidPictureType(String path) {
        waitUntilWordsLoaded();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setSource(ImageSource.newBuilder().setImageUri(path).build()).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION))
                        .setImage(img)
                        .build();
        requests.add(request);
            boolean result = false;
            // Perform the request
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            List<AnnotateImageResponse> resultsFromSearch = new ArrayList<>();

            // Display the results
            for (AnnotateImageResponse res : responses) {
                for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
                      if (acceptedImages.contains(entity.getName().toLowerCase())) {
                        resultsFromSearch.add(res);
                    }
                }
            }
            return resultsFromSearch;

    }

    @Override
    public List<AnnotateImageResponse> getContentOfThePicture(String path) {
        waitUntilWordsLoaded();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setSource(ImageSource.newBuilder().setImageUri(path).build()).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION))
                        .setImage(img)
                        .build();
        requests.add(request);
        boolean result = false;
        // Perform the request
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();
        List<AnnotateImageResponse> resultsFromSearch = new ArrayList<>();

        // Display the results
        for (AnnotateImageResponse res : responses) {
            for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
                    resultsFromSearch.add(res);
            }
        }
        return resultsFromSearch;
    }

    @SneakyThrows
    @Override
    public Boolean getSafeSearch(String path) {
        waitUntilWordsLoaded();
        List<AnnotateImageRequest> requests = new ArrayList<>();

        Image img = Image.newBuilder().setSource(ImageSource.newBuilder().setImageUri(path).build()).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return false;
                }
                SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();
                if(getIfConditionMatches(annotation.getAdult(), annotation.getSpoof(), annotation.getViolence(),
                        annotation.getMedical(), annotation.getRacy())){
                    return true;
                }else{
                    return false;
                }
            }
            return false;

        }
    }

    private boolean getIfConditionMatches(Likelihood... likelihoods){
        for(Likelihood likelihood : likelihoods){
            if(likelihood == Likelihood.VERY_LIKELY || likelihood == Likelihood.LIKELY || likelihood == Likelihood.POSSIBLE){
                return true;
            }
        }
        return false;
    }

    private void waitUntilWordsLoaded(){
        if(!areWordsLoaded){
            while (!areWordsLoaded){
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            this.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

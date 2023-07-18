package delco.twitter.scraping.services.implementations;


import com.google.protobuf.ByteString;
import delco.twitter.scraping.services.interfaces.VisionAPIService;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.util.*;

@Service
public class VisionAPIServiceImpl extends Thread implements VisionAPIService {

    private Set<String> acceptedImages;
    private Set<String> acceptedLabels;
    private Set<String> notAcceptedImages;
    boolean areWordsLoaded = false;
//    private ImageAnnotatorClient client;
//


    @SneakyThrows
    public VisionAPIServiceImpl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                acceptedImages = readFiles("acceptedFilesDat"
                        +File.separator+"acceptedObjectSearch");
                notAcceptedImages = readFiles("notAcceptedFilesDat"
                        +File.separator+"notAcceptedObjectSearch");
                acceptedLabels = readFiles("acceptedFilesDat"
                        +File.separator+"acceptedLabelSearch");
                areWordsLoaded = true;
            }
        }).start();
//        try{
//            client = ImageAnnotatorClient.create();
//        }catch (RuntimeException e){ }

    }

    /**
     * This methdod is used to load the .dat files from the resources folder, which contains all the labels that
     * are accepted or not in order to determine wheter an image contains animals and/or nature
     * @param fileName The name of the file, without the .dat
     * @return A tree set that is going to be assigned to the class variable acceptedImages/notAcceptedImages
     */
    public HashSet<String> readFiles(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource("wordsList"+File.separator+fileName+".dat");
            InputStream inputStream = classPathResource.getInputStream();
            File somethingFile = File.createTempFile("test", ".txt");
            try {
                FileUtils.copyInputStreamToFile(inputStream, somethingFile);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(somethingFile));
            return (HashSet<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("The file was not found");
    }

    @SneakyThrows
    @Override
    public List<String> getValidPictureType(String path) {
        List<String> resultsFromSearch = new ArrayList<>();
//        waitUntilWordsLoaded();
//        List<AnnotateImageRequest> requests = new ArrayList<>();
//        Image img = Image.newBuilder().setSource(ImageSource.newBuilder().setImageUri(path).build()).build();
//        AnnotateImageRequest request =
//                AnnotateImageRequest.newBuilder()
//                        .addFeatures(Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION))
//                        .setImage(img)
//                        .build();
//        requests.add(request);
//
//        List<AnnotateImageResponse> responses = client.batchAnnotateImages(requests).getResponsesList();
//        List<String> resultsFromSearch = new ArrayList<>();
//
//        // Display the results
//        for (AnnotateImageResponse res : responses) {
//            for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
//                if (acceptedImages.contains(entity.getName().toLowerCase())) {
//                    resultsFromSearch.add(entity.getName());
//                }
//            }
//        }
        return resultsFromSearch;
    }

    @Override
    public List<String> detectLabels(String url) {
//        List<AnnotateImageRequest> requests = new ArrayList<>();
        List<String> labels = new ArrayList<>();

//        Image img = Image.newBuilder().setSource(ImageSource.newBuilder().setImageUri(url).build()).build();
//        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
//        AnnotateImageRequest request =
//                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
//        requests.add(request);
//
//        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
//        List<AnnotateImageResponse> responses = response.getResponsesList();
//
//        for (AnnotateImageResponse res : responses) {
//            if (res.hasError()) {
//                return new ArrayList<>();
//            }
//            // For full list of available annotations, see http://g.co/cloud/vision/docs
//            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
//                labels.add(annotation.getDescription());
//            }
//        }
        return labels.subList(0,Math.min(labels.size(),10));
    }


    @SneakyThrows
    @Override
    public Boolean getSafeSearch(String path) {
        waitUntilWordsLoaded();
//        List<AnnotateImageRequest> requests = new ArrayList<>();
//
//        Image img = Image.newBuilder().setSource(ImageSource.newBuilder().setImageUri(path).build()).build();
//        Feature feat = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();
//        AnnotateImageRequest request =
//                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
//        requests.add(request);
//
//        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
//        List<AnnotateImageResponse> responses = response.getResponsesList();
//
//        for (AnnotateImageResponse res : responses) {
//            if (res.hasError()) {
//                System.out.format("Error: %s%n", res.getError().getMessage());
//                return false;
//            }
//            SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();
//            return getIfConditionMatches(annotation.getAdult(), annotation.getViolence(),
//                    annotation.getMedical(), annotation.getRacy());
//        }
        return false;

    }

//    private boolean getIfConditionMatches(Likelihood... likelihoods){
//        for(Likelihood likelihood : likelihoods){
//            if(likelihood == Likelihood.VERY_LIKELY || likelihood == Likelihood.LIKELY || likelihood == Likelihood.POSSIBLE){
//                return true;
//            }
//        }
//        return false;
//    }

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

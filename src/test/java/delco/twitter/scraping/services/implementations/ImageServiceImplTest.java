package delco.twitter.scraping.services.implementations;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(Parameterized.class)
class ImageServiceImplTest {

    private ImageServiceImpl imageService;


    @BeforeEach
    void setUp(){
        imageService = new ImageServiceImpl();
    }


    /**
     * These test and the Source method attached to it were made to check if the algorithm that detects wether an images
     * contains content from nature or animals word. The Path of the generatePostivePaths is the folder where 
     * the nature/animals picture are stored, so the test iterates over each picture and compare the result
     * @param arg The absolute path to the image
     */
    @ParameterizedTest
    @MethodSource("generatePostivePaths")
    void D(String arg) {
        System.out.println(arg);
        assertTrue(imageService.getImageContent(arg));
    }

    public static Stream<String> generatePostivePaths(){
        File directorioPositivo = new File("C:\\Users\\chris\\OneDrive\\Desktop\\Accepted");
        List<String> pathList = new ArrayList<>();
        Arrays.stream(directorioPositivo.listFiles()).forEach(file -> pathList.add(file.getAbsolutePath()));
        return pathList.stream();
    }

    /**
     * This test and the Source method attached to it were made to check if the algorithm that detects wether an images
     * doesn't contains content from nature or animals word. The Path of the generateNegativePaths is the folder where 
     * the nature/animals picture are stored, so the test iterates over each picture and compare the result
     * @param arg The absolute path to the image
     */
    @ParameterizedTest
    @MethodSource("generateNegativePaths")
    void getImageContentFail(String arg) {
        System.out.println(arg);
        assertFalse(imageService.getImageContent(arg));
    }

    public static Stream<String> generateNegativePaths(){
        File negativeDirectory = new File("C:\\Users\\chris\\OneDrive\\Desktop\\NotAccepted");
        List<String> pathList = new ArrayList<>();
        Arrays.stream(negativeDirectory.listFiles()).forEach(file -> pathList.add(file.getAbsolutePath()));
        return pathList.stream();
    }


    /**
     * This test and the method attached to it does the same iteration over the folder as de getImageContentFail
     * and getImageContent, but it analyze the image in order to detect if the image is Grotesque or not
     * @param arg The absolute path to the Image
     */
    @ParameterizedTest
    @MethodSource("generateGrotesquePaths")
    void getAdultContent(String arg){
        assertTrue(imageService.detectAdult(arg));
    }

    public static Stream<String> generateGrotesquePaths(){
        File grostesqueDirectory = new File("C:\\Users\\chris\\OneDrive\\Desktop\\GrotesqueImages");
        List<String> pathList = new ArrayList<>();
        Arrays.stream(grostesqueDirectory.listFiles()).forEach(file -> pathList.add(file.getAbsolutePath()));
        return pathList.stream();
    }

    /**
     * Same as the getAdultContent, but to check if returns false, because in NotGrotesqueImages there aren't any
     * violent/racy/adult pictures
     * @param arg The absolute path to the Image
     */
    @ParameterizedTest
    @MethodSource("generateNotGrotesquePaths")
    void getNotAdultContent(String arg){
        assertFalse(imageService.detectAdult(arg));
    }

    public static Stream<String> generateNotGrotesquePaths(){
        File grostesqueDirectory = new File("C:\\Users\\chris\\OneDrive\\Desktop\\NotGrotesqueImages");
        List<String> pathList = new ArrayList<>();
        Arrays.stream(grostesqueDirectory.listFiles()).forEach(file -> pathList.add(file.getAbsolutePath()));
        return pathList.stream();
    }


    /**
     * This test was made in order to check if the images is correctly donwloaded from the internet
     */
    @Test
    void readImageFromInternet(){
        assertFalse(imageService.detectAdult("https://pbs.twimg.com/media/FRS9VLzXMAg9-66?format=jpg&name=medium"));
    }


    @Test
    void downloadImage() {
        try {
            URL url = new URL("https://pbs.twimg.com/media/FRS9VLzXMAg9-66?format=jpg&name=medium");
            assertNotNull(imageService.downloadImage(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
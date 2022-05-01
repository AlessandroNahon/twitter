package delco.twitter.scraping.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(Parameterized.class)
class VisionAPIServiceImplTest extends Thread{

    private VisionAPIServiceImpl visionAPIService;

    @BeforeEach
    void setup(){
        visionAPIService = new VisionAPIServiceImpl();
    }


    public static Stream<String> generatePostivePaths(){
        File directorioPositivo = new File("C:\\Users\\chris\\OneDrive\\Desktop\\Thesaurus\\Images\\Accepted");
        List<String> pathList = new ArrayList<>();
        Arrays.stream(directorioPositivo.listFiles()).forEach(file -> pathList.add(file.getAbsolutePath()));
        return pathList.stream();
    }


    @ParameterizedTest
    @MethodSource("generatePostivePaths")
    void getPictureType(String arg) {
        boolean result = visionAPIService.getPictureType(arg);
        try {
            this.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(result);
    }

    public static Stream<String> generateNegativePaths(){
        File directorioPositivo = new File("C:\\Users\\chris\\OneDrive\\Desktop\\Thesaurus\\Images\\NotAccepted");
        List<String> pathList = new ArrayList<>();
        Arrays.stream(directorioPositivo.listFiles()).forEach(file -> pathList.add(file.getAbsolutePath()));
        return pathList.stream();
    }


    @ParameterizedTest
    @MethodSource("generateNegativePaths")
    void getNegative(String arg) {
        boolean result = visionAPIService.getPictureType(arg);
        assertFalse(result);

    }


    @ParameterizedTest
    @MethodSource("getGrotesquePaths")
    void getSafeSearch(String arg) {
        boolean result = visionAPIService.getSafeSearch(arg);
        try {
            this.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(result);
    }

    public static Stream<String> getGrotesquePaths(){
        List<String> paths = new ArrayList<>();
        Arrays.stream(
                new File("C:\\Users\\chris\\OneDrive\\Desktop\\Thesaurus\\Images\\GrotesqueImages")
                        .listFiles()).forEach(file -> paths.add(file.getAbsolutePath()));
        return paths.stream();
    }

    @Test
    void getResultFromSearch(){
        assertTrue(visionAPIService.getPictureType("https://pbs.twimg.com/media/FRj1YCNWYAI5pv9?format=jpg&name=small"));

    }


}
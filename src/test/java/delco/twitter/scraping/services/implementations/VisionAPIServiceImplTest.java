package delco.twitter.scraping.services.implementations;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.LocalizedObjectAnnotation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(Parameterized.class)
class VisionAPIServiceImplTest extends Thread{

    @Autowired
    private VisionAPIServiceImpl visionAPIService;

    @BeforeEach
    void setup(){
        visionAPIService = new VisionAPIServiceImpl();
    }

    @ParameterizedTest
    @MethodSource("validUrls")
    void checkListOfObjects(String arg){
        List<String> responseList = visionAPIService.getValidPictureType(arg);
        responseList.forEach(System.out::println);
        assertFalse(responseList.isEmpty());
    }

    public static List<String> validUrls(){
        try {
            return new ArrayList<>(Files.readAllLines(Paths.get("C:\\Users\\chris\\OneDrive\\Desktop\\links.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

//
//    @SneakyThrows
//    @Test
//    void checkImageLabels(){
//        List<String> list = new ArrayList<>();
//        for(File f : new File("C:\\Users\\chris\\OneDrive\\Desktop\\Thesaurus\\Images\\Accepted").listFiles()){
//            list.addAll(visionAPIService.detectLabels(f.getAbsolutePath()));
//            Thread.sleep(250);
//        }
//        Set<String> listOfDescription = new HashSet<>(list);
//        listOfDescription.forEach(System.out::println);
//        assertFalse(listOfDescription.isEmpty());
//    }

    public static List<String> getValidPictureType(){
        List<String> list = new ArrayList<>();
        for(File f : new File("C:\\Users\\chris\\OneDrive\\Desktop\\Thesaurus\\Images\\Accepted").listFiles()){
            list.add(f.getAbsolutePath());
        }
        return list;
    }

}
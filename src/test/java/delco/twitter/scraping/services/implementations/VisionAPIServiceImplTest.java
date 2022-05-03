package delco.twitter.scraping.services.implementations;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.LocalizedObjectAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(Parameterized.class)
class VisionAPIServiceImplTest extends Thread{

    @Autowired
    private VisionAPIServiceImpl visionAPIService;

    @BeforeEach
    void setup(){
        visionAPIService = new VisionAPIServiceImpl();
    }

    @Test
    void checkListOfObjects(){
        String url = "https://pbs.twimg.com/media/FRV057aXEAAGlWs?format=jpg&name=medium";
        List<AnnotateImageResponse> responseList = visionAPIService.getValidPictureType(url);
        System.out.println(responseList);
        for(AnnotateImageResponse air : responseList){
            for(LocalizedObjectAnnotation ta : air.getLocalizedObjectAnnotationsList()){
                System.out.println(ta.getName() + "  " + ta.getScore());
            }
        }
        assertNotEquals(responseList,new ArrayList<AnnotateImageResponse>());
    }

}
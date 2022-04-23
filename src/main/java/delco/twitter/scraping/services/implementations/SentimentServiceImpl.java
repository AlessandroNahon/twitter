package delco.twitter.scraping.services.implementations;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import delco.twitter.scraping.repositories.SentimentRepository;
import delco.twitter.scraping.services.interfaces.SentimentService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SentimentServiceImpl implements SentimentService {

    private final SentimentRepository sentimentRepository;

    @SneakyThrows
    public SentimentServiceImpl(SentimentRepository sentimentRepository){
        this.sentimentRepository = sentimentRepository;
        TransformerFactory.newInstance().setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }

    @Override
    public SentimentEnum getSentiment(String texto) {
        try {
            LanguageServiceClient language = LanguageServiceClient.create();
            Document doc = Document.newBuilder().setContent(texto).setType(Document.Type.PLAIN_TEXT).build();
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
            if(sentiment.getScore() > 0.1 && sentiment.getScore() < 0.5) {
                addAppearance(1L);
                return SentimentEnum.POSITIVE;
            }else if (sentiment.getScore() > 0.5 && sentiment.getScore() < 0.9) {
                addAppearance(2L);
                return SentimentEnum.VERY_POSITIVE;
            }else if(sentiment.getScore() < -0.1 && sentiment.getScore() > -0.5) {
                addAppearance(4L);
                return SentimentEnum.NEGATIVE;
            }else if(sentiment.getScore() > -0.5 && sentiment.getScore() < -0.9) {
                addAppearance(5L);
                return SentimentEnum.VERY_NEGATIVE;
            }else{
                addAppearance(3L);
                return SentimentEnum.NEUTRAL;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        addAppearance(3L);
        return SentimentEnum.NEUTRAL;
    }

    @Override
    public void addAppearance(Long id) {
        delco.twitter.scraping.model.Sentiment s = sentimentRepository.findById(id).orElse(null);
        s.setAppearances(s.getAppearances() + 1);
        sentimentRepository.save(s);
    }

    @Override
    public List<Integer> getAppearances() {
        List<Integer> appearances = new ArrayList<>();
        Iterable<delco.twitter.scraping.model.Sentiment> sentiments = sentimentRepository.findAll();
        for(delco.twitter.scraping.model.Sentiment s : sentiments){
            appearances.add(s.getAppearances());
        }
        return appearances;
    }


}

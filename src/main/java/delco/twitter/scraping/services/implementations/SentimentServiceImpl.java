package delco.twitter.scraping.services.implementations;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import delco.twitter.scraping.repositories.SentimentRepository;
import delco.twitter.scraping.services.interfaces.SentimentService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerFactory;
import java.util.ArrayList;
import java.util.List;

@Service
public class SentimentServiceImpl implements SentimentService {

    @Autowired
    private SentimentRepository sentimentRepository;
    private final LanguageServiceClient languageServiceClient;

    @SneakyThrows
    public SentimentServiceImpl(){
        this.languageServiceClient = LanguageServiceClient.create();
        TransformerFactory.newInstance().setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }

    /**
     * Receiving a text, this method calls the Google Cloud NLP and analize the sentiment of the text. Depending
     * on the Score, this methods determine the correct SentimentEnum and gives it back so you can assign in to a
     * tweet/reply.
     * @param texto Text to be analyzed
     * @return SentimentEnum
     */
    @Override
    public SentimentEnum getSentiment(String texto) {
        Document doc = Document.newBuilder().setContent(texto).setType(Document.Type.PLAIN_TEXT).build();
        Sentiment sentiment = languageServiceClient.analyzeSentiment(doc).getDocumentSentiment();
        if(sentiment.getScore() > 0.1 && sentiment.getScore() <= 0.5) {
            addAppearance(1L);
            return SentimentEnum.POSITIVE;
        }else if (sentiment.getScore() > 0.5 && sentiment.getScore() < 0.9) {
            addAppearance(2L);
            return SentimentEnum.VERY_POSITIVE;
        }else if(sentiment.getScore() < -0.1 && sentiment.getScore() >= -0.5) {
            addAppearance(4L);
            return SentimentEnum.NEGATIVE;
        }else if(sentiment.getScore() > -0.5 && sentiment.getScore() < -0.9) {
            addAppearance(5L);
            return SentimentEnum.VERY_NEGATIVE;
        }else{
            addAppearance(3L);
            return SentimentEnum.NEUTRAL;
        }
    }

    /**
     * This method is called to add 1 to the appearances of the sentiment.
     * @param id Id of the sentiment
     */
    @Override
    public void addAppearance(Long id) {
        delco.twitter.scraping.model.Sentiment s = sentimentRepository.findById(id).orElse(null);
        s.setAppearances(s.getAppearances() + 1);
        sentimentRepository.save(s);
    }

    /**
     * This method is called to get all the apparecnes of the different sentiments sentiment.
     * @return List of Integer, correspondiing to each sentiment.
     */
    @Override
    public Iterable<delco.twitter.scraping.model.Sentiment> findAllSentiment() {
        return sentimentRepository.findAll();
    }


}

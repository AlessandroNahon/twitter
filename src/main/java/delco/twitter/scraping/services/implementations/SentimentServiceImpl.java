package delco.twitter.scraping.services.implementations;


import delco.twitter.scraping.model.Sentiments;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import delco.twitter.scraping.repositories.SentimentRepository;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.TweetService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SentimentServiceImpl implements SentimentService {

    @Autowired
    private SentimentRepository sentimentRepository;

    @Autowired
    @Lazy
    private TweetService tweetService;

    @Autowired
    @Lazy
    private RepliesService repliesService;

//    private final LanguageServiceClient languageServiceClient;

    @SneakyThrows
    public SentimentServiceImpl(){
//        this.languageServiceClient = LanguageServiceClient.create();
//        TransformerFactory.newInstance().setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }

    /**
     * Receiving a text, this method calls the Google Cloud NLP and analize the sentiment of the text. Depending
     * on the Score, this methods determine the correct SentimentEnum and gives it back so you can assign in to a
     * tweet/reply.
     * @param texto Text to be analyzed
     * @return SentimentEnum
     */
    @Override
    public SentimentEnum getSentiment(String texto, String originalUsername, String belongsTo) {
//        Document doc = Document.newBuilder().setContent(texto).setType(Document.Type.PLAIN_TEXT).build();
//        Sentiment sentiment = null;
//        try{
//             sentiment = languageServiceClient.analyzeSentiment(doc).getDocumentSentiment();
//            SentimentEnum sentimentEnum = SentimentEnum.POSITIVE;
//            if(sentiment.getScore() >= -0.25 && sentiment.getScore() <= 0.25) {
//                sentimentEnum = SentimentEnum.NEUTRAL;
//            }else if(sentiment.getScore() < -0.25){
//                sentimentEnum = SentimentEnum.NEGATIVE;
//            }
//            addAppearance(sentimentEnum, originalUsername, belongsTo);
//            return sentimentEnum;
//        }catch (Exception ex){
//            System.out.println("Error: " + ex.getMessage());
//        }
        return SentimentEnum.NEUTRAL;
    }


    @Override
    public void addAppearance(SentimentEnum sentiment, String organization, String belongsTo) {
        Sentiments s =
                sentimentRepository.findSpecificSentiment(belongsTo,organization,sentiment.name());
        if(s != null) {
            s.setAppearances(s.getAppearances() + 1);
        }else{
           s = Sentiments.builder().sentiment(sentiment.name())
                   .organization(organization).belongs_to(belongsTo).appearances(1).build();
        }
        sentimentRepository.save(s);
    }

    @Override
    public List<Sentiments> getSentimentsByOrganizationAndBelongs(String originalUsername, String belongsTo) {
        List<Sentiments> sentimentsList = sentimentRepository.findAllSentimentsByOrganizationAndBelongsTo(belongsTo,originalUsername);
        sentimentsList.sort(Comparator.comparing(Sentiments::getSentiment));
        return sentimentsList;
    }







}

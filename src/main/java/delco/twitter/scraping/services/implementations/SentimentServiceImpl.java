package delco.twitter.scraping.services.implementations;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import delco.twitter.scraping.repositories.RepliesRepository;
import delco.twitter.scraping.repositories.SentimentRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.repositories.WordRepository;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.WordService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.websocket.reactive.TomcatWebSocketReactiveWebServerCustomizer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Integer> analyzeDatabaseByTypeAndClassification(String classification) {
        List<Integer> list = new ArrayList<>();
        if(classification.equals("Sentimental")) {
            list.add(tweetService.getCountTweetsPositiveTextAndPositiveImage() + repliesService.getCountReplyPositiveTextAndPositiveImage());
            list.add(tweetService.getCountTweetsPositiveTextAndPositiveEmojis() + repliesService.getCountReplysPositiveTextAndPositiveEmojis());
            list.add(tweetService.getCountFullMatchesTweets() + repliesService.getCountFullMatchesReply());
        }else if(classification.equals("Disruptive")){
            list.add(tweetService.getCountTweetsNegativeTextAndNegativeImage() + repliesService.getCountReplyNegativeTextAndNegativeImage());
            list.add(tweetService.getCountTweetsNegativeTextAndNegativeEmojis() + repliesService.getCountReplysNegativeTextAndNegativeEmojis());
            list.add(tweetService.getCountFullNegativeMatchesTweets() + repliesService.getCountFullNegativeMatchesReply());
        }
        return list;
    }



    @Override
    public List<Tweet> findAllOtherTweets() {
        List<Tweet> fullList = tweetService.findAllTweets();
        fullList.removeAll(tweetService.getFullMatchesTweets());
        fullList.removeAll(tweetService.getFullNegativeMatchesTweets());
        fullList.removeAll(tweetService.getTweetsNegativeTextAndNegativeEmojis());
        fullList.removeAll(tweetService.getTweetsNegativeTextAndNegativeImage());
        fullList.removeAll(tweetService.getTweetsPositiveTextAndPositiveEmojis());
        fullList.removeAll(tweetService.getTweetsPositiveTextAndPositiveImage());
        return fullList;
    }

    @Override
    public List<Reply> findAllOtherReply() {
        List<Reply> fullList = repliesService.findAllReplies();
        fullList.removeAll(repliesService.getFullMatchesReply());
        fullList.removeAll(repliesService.getFullNegativeMatchesReply());
        fullList.removeAll(repliesService.getReplyNegativeTextAndNegativeEmojis());
        fullList.removeAll(repliesService.getReplyNegativeTextAndNegativeImage());
        fullList.removeAll(repliesService.getReplyPositiveTextAndPositiveEmojis());
        fullList.removeAll(repliesService.getReplyPositiveTextAndPositiveImage());
        return fullList;
    }

    @Override
    public List<Tweet> getTweetsBySearchAndLookingFor(String search, String lookingFor) {
        if (search.equals("Sentimental")) {
            if (lookingFor.equals("Img & Sentiment")) {
                return tweetService.getTweetsPositiveTextAndPositiveImage();
            } else if (lookingFor.equals("Emoji & Sentiment")) {
                return tweetService.getTweetsPositiveTextAndPositiveEmojis();
            } else {
                return tweetService.getFullMatchesTweets();
            }
        } else if (search.equals("Disruptive")) {
            if (lookingFor.equals("Img & Sentiment")) {
                return tweetService.getTweetsNegativeTextAndNegativeImage();
            } else if (lookingFor.equals("Emoji & Sentiment")) {
                return tweetService.getTweetsNegativeTextAndNegativeEmojis();
            } else {
                return tweetService.getFullNegativeMatchesTweets();
            }
        } else {
            return findAllOtherTweets();
        }
    }

    @Override
    public List<Reply> getRepliesBySearchAndLookingFor(String search, String lookingFor) {
            if(search.equals("Sentimental")){
                if(lookingFor.equals("Img & Sentiment")){
                    return repliesService.getReplyPositiveTextAndPositiveImage();
                }else if(lookingFor.equals("Emoji & Sentiment")) {
                    return repliesService.getReplyPositiveTextAndPositiveEmojis();
                }else{
                    return repliesService.getFullMatchesReply();
                }
            }else if(search.equals("Disruptive")){
                if(lookingFor.equals("Img & Sentiment")){
                    return repliesService.getReplyNegativeTextAndNegativeImage();
                }else if(lookingFor.equals("Emoji & Sentiment")) {
                    return repliesService.getReplyNegativeTextAndNegativeEmojis();
                }else{
                    return repliesService.getFullNegativeMatchesReply();
                }
            }else{
                return findAllOtherReply();
            }
    }


}

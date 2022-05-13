package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.utils.DatumConverters;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TweetServiceImpl extends Thread implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private TwitterAPIService twitterAPIService;

    @Autowired
    private RepliesService repliesService;

    @Autowired
    private DatumConverters datumConverters;


    public TweetServiceImpl() {}


    /**
     * This method is used to parse the Root object that comes from the Twitter API and create different
     * tweet objects with the corresponding information about images, replies, sentiment and words.
     * @param root Root object that comes from the Twitter API
     * @param username Username of the user that is being scraped, to set it to the created tweet
     */
    @Override
    public synchronized void parseTweetDatumFromRoot(Root root,String username) {
        System.out.println(root.toString());
        root.getData().forEach(datum -> {
                List<Images> images = imageService.getImages(root.getIncludes(),datum);
                if(!images.isEmpty()) {
                    System.out.println("Hay un tweet que es válido");
                    Tweet tweet = datumConverters.convertDatumToTweet(datum,username);
                    tweet.setUsername(username);
                    tweetRepository.save(tweet);
                    images.forEach(img -> {
                        img.setTweet(tweet);
                        imageService.addLabelsAndSaveImage(img);
                    });
                    repliesService.parseReplyFromTweet(
                            twitterAPIService.getReplies(tweet.getConversationId(),datum.getId()), tweet);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wordService.analyzeText(datum.getText(),WordServiceImpl.TWEET_BELONGS_TO,username);
                        }
                    }).start();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        });
    }

    @Transactional
    @Override
    public void deleteAllInfo(Long id) {
        tweetRepository.deleteById(id);
        repliesService.deleteByTweetId(id);
        imageService.deleteByTweetId(id);
    }

    @Override
    public void changeSentiment(String sentiment, Long id) {
        Tweet tweet = tweetRepository.findById(id).get();
        switch (sentiment.toLowerCase()) {
            case  "positive":
                tweet.setTextSentiment(SentimentEnum.POSITIVE);
                break;
            case "negative":
                tweet.setTextSentiment(SentimentEnum.NEGATIVE);
                break;
            case "neutral":
                tweet.setTextSentiment(SentimentEnum.NEUTRAL);
                break;
            case "very positive":
                tweet.setTextSentiment(SentimentEnum.VERY_POSITIVE);
                break;
            case "very negative":
                tweet.setTextSentiment(SentimentEnum.VERY_NEGATIVE);
                break;
        }
        tweetRepository.save(tweet);
    }

    /**
     * This method iterates over the text of the tweet and the text of its replies to gather all the emojis
     * that are contained in the text of those objects
     * @param t The tweet that is being analyzed
     * @return A Set<String> with all the emojis. A set is used so the emojis aren't repeated
     */
    @Override
    public Set<String> getAllEmojisFromTweets(Tweet t){
        Set<String> emojisList = new HashSet<>(new HashSet<>(wordService.getAllEmojisFromText(t.getText())));
        for(Reply r : t.getReplies()){
            emojisList.addAll(new HashSet<>(wordService.getAllEmojisFromText(r.getText())));
        }
        return emojisList;
    }

    @Override
    public List<Tweet> findBySentiment(String sentiment) {
        switch (sentiment.toLowerCase()){
            case "positive":
                return tweetRepository.findAllByTextSentiment(SentimentEnum.POSITIVE);
            case "negative":
                return tweetRepository.findAllByTextSentiment(SentimentEnum.NEGATIVE);
            case "neutral":
                return tweetRepository.findAllByTextSentiment(SentimentEnum.NEUTRAL);
            case "very positive":
                return tweetRepository.findAllByTextSentiment(SentimentEnum.VERY_POSITIVE);
            default:
                return tweetRepository.findAllByTextSentiment(SentimentEnum.VERY_NEGATIVE);
        }
    }

    @Override
    public List<Tweet> findByUsername(String username) {
        return tweetRepository.findByUsername(username);
    }

    @Override
    public List<Tweet> findByImageContent(String imageContent) {
        if(imageContent.equals("Grotesque")){
            return tweetRepository.findByImageContent(TypeEnum.GROTESQUE.name());
        }else{
            return tweetRepository.findByImageContent(TypeEnum.KITSCH.name());
        }
    }

    @Override
    public List<Tweet> findTextImage(String username, boolean wantPositive) {
        if (wantPositive) {
            return tweetRepository.getTextImagePositive(username);
        }else{
            return tweetRepository.getTextImageNegative(username);
        }
    }

    @Override
    public List<Tweet> findText(String username, boolean wantPositive) {
        if(wantPositive){
            return tweetRepository.getTextPositive(username);
        }else{
            return tweetRepository.getTextNegative(username);
        }
    }

    @Override
    public List<Tweet> findTextEmoji(String username, boolean wantPositive) {
        List<Tweet> tweetList = wantPositive ? findText(username,true) : findText(username,false);
        List<String> tweetListEmoji = wantPositive ?
                wordService.getByBelongsToAndOrganizationBySyntax("Tweet",TypeEnum.KITSCH_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList()) :
                wordService.getByBelongsToAndOrganizationBySyntax("Tweet",TypeEnum.GROTESQUE_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList());
        Set<Tweet> tweetListEmojiFiltered = new HashSet<>();
        for(Tweet t : tweetList){
            List<String> emojisFromTweet = wordService.getAllEmojisFromText(t.getText());
            for(String s : emojisFromTweet){
                if(tweetListEmoji.contains(s)){
                    tweetListEmojiFiltered.add(t);
                    break;
                }
            }
        }
        return new ArrayList<>(tweetListEmojiFiltered);
    }
    @Override
    public List<Tweet> findFullMatches(String username, boolean wantPositive) {
        List<Tweet> tweetList = wantPositive ? findTextImage(username,true) : findTextImage(username,false);
        List<String> tweetListEmoji = wantPositive ?
                wordService.getByBelongsToAndOrganizationBySyntax("Tweet",TypeEnum.KITSCH_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList()) :
                wordService.getByBelongsToAndOrganizationBySyntax("Tweet",TypeEnum.GROTESQUE_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList());
        Set<Tweet> tweetListEmojiFiltered = new HashSet<>();
        for(Tweet t : tweetList){
            List<String> emojisFromTweet = wordService.getAllEmojisFromText(t.getText());
            for(String s : emojisFromTweet){
                if(tweetListEmoji.contains(s)){
                    tweetListEmojiFiltered.add(t);
                    break;
                }
            }
        }
        return new ArrayList<>(tweetListEmojiFiltered);
    }

    @Override
    public List<Tweet> findAllOthers(String username) {
        List<Tweet> tweetList = tweetRepository.findByUsername(username);
        tweetList.removeAll(findTextImage(username,true));
        tweetList.removeAll(findTextEmoji(username,true));
        tweetList.removeAll(findFullMatches(username,true));
        tweetList.removeAll(findTextImage(username,false));
        tweetList.removeAll(findTextEmoji(username,false));
        tweetList.removeAll(findFullMatches(username,false));
        return tweetList;
    }

    @Override
    public List<Tweet> getCountBySentiment(String username,boolean wantPositive) {
        Set<Tweet> tweetSet = new HashSet<>();
        tweetSet.addAll(findTextEmoji(username,wantPositive));
        tweetSet.addAll(findTextImage(username, wantPositive));
        tweetSet.addAll(findFullMatches(username, wantPositive));
        return new ArrayList<>(tweetSet);
    }


    /**
     * This method is used by the controller to call the repository and find all the tweets that contains
     * the word passed by parameter
     * @param text The word used in the search
     * @return List with all the Tweets that contains the "text", empty list if there's no tweets containing that text
     */
    @Override
    public List<Tweet> findByText(String text, String username) {
        try{
            return tweetRepository.findAllByTextContaining(text,username);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
        return new ArrayList<Tweet>();
    }
    /**
     * This metdhod uses the repository to get a list of all the present tweets in the database
     * @return
     */
    @Override
    public List<Tweet> findAllTweets() {
        return tweetRepository.findAll();
    }



}

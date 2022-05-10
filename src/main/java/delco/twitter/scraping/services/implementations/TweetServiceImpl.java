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
                    Tweet tweet = datumConverters.convertDatumToTweet(datum);
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
                            wordService.analyzeText(datum.getText(),WordServiceImpl.TWEET_BELONGS_TO);
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


    /**
     * This method is used by the controller to call the repository and find all the tweets that contains
     * the word passed by parameter
     * @param text The word used in the search
     * @return List with all the Tweets that contains the "text", empty list if there's no tweets containing that text
     */
    @Override
    public List<Tweet> findByText(String text) {
        try{
            return tweetRepository.findAllByTextContaining(text);
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


    // =============================================
    //           FIND POSITIVE CONTENT
    // =============================================


    /**
     * This method is used to get all the Tweet that contains text with positive/very_positive sentiment
     * and the content of the Image is kistch
     * @return List<Tweet> that matches both conditions
     */
    @Override
    public List<Tweet> getTweetsPositiveTextAndPositiveImage() {
       return tweetRepository.getTextImagePositive();
    }

    /**
     * This method calls the getTweetsPositiveTextAndPositiveImage() and returns the size of the list returned by
     * that method.
     * @return Integer with the size of the list of Tweets that has postive/very_positive sentiment and positive image
     */
    @Override
    public Integer getCountTweetsPositiveTextAndPositiveImage() {
        return tweetRepository.getTextImagePositive().size();
    }

    /**
     * This method is used to filter the tweets that has positive Text and positive Emojis. To achieve this, we
     * fisrt get all the tweets that has positive/very_positive text, and then we compare the emojis of
     * those tweets with the kistch emojis. If there is at least one positive emoji in the tweet, we add it to the list
     * @return List<Tweet> with positive emojis in the text
     */
    @Override
    public List<Tweet> getTweetsPositiveTextAndPositiveEmojis() {
        List<Tweet> tweets = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.TWEET_BELONGS_TO,
                TypeEnum.KITSCH_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Tweet t : tweetRepository.getTextPositive()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    tweets.add(t);
                    break;
                }
            }
        }
        return tweets;
    }

    /**
     * This method calls the getTweetsPositiveTextAndPositiveEmojis() and returns the size of the list returned by
     * @return Integer with the size of the list of Tweets that has postive/very_positive sentiment and positive emojis
     */
    @Override
    public Integer getCountTweetsPositiveTextAndPositiveEmojis() {
        return getTweetsPositiveTextAndPositiveEmojis().size();
    }

    /**
     * This method is similar to the getTweetsPositiveTextAndPositiveEmojis(), with the difference that this method
     * compares the emojis with the list of tweets returned by the method getTweetsPositiveTextAndPositiveImage() instead
     * of getTextPositive()
     * @return The list of tweets that contains positive text, positive emojis and positive images
     */
    @Override
    public List<Tweet> getFullMatchesTweets() {
        List<Tweet> tweets = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.TWEET_BELONGS_TO,
                TypeEnum.KITSCH_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Tweet t : tweetRepository.getTextImagePositive()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    tweets.add(t);
                    break;
                }
            }
        }
        return tweets;
    }


    /**
     * This method calls the getFullMatchesTweets() and returns the size of the list returned by
     * @return Integer with the size of the list of Tweets that contains positive text, positive emojis and positive images
     */
    @Override
    public Integer getCountFullMatchesTweets() {
        return getFullMatchesTweets().size();
    }






    // =============================================
    //           FIND NEGATIVE CONTENT
    // =============================================




    /**
     * This method is used to get all the Tweet that contains text with negative/very_negative sentiment
     * and the content of the Image is Grotesque
     * @return List<Tweet> that matches both conditions
     */
    @Override
    public List<Tweet> getTweetsNegativeTextAndNegativeImage() {
        return tweetRepository.getTextImageNegative();
    }

    /**
     * This method calls the getTweetsNegativeTextAndNegativeImage() and returns the size of the list returned by
     * that method.
     * @return Integer with the size of the list of Tweets that has negative/very_negative sentiment and Grotesque image
     */
    @Override
    public Integer getCountTweetsNegativeTextAndNegativeImage() {
        return tweetRepository.getTextImageNegative().size();
    }


    /**
     * This method is used to filter the tweets that has negative Text and negative Emojis. To achieve this, we
     * fisrt get all the tweets that has negative/very_negative text, and then we compare the emojis of
     * those tweets with the Grotesque emojis. If there is at least one grotesque emoji in the tweet, we add it to the list
     * @return List<Tweet> with grotesque emojis in the text
     */
    @Override
    public List<Tweet> getTweetsNegativeTextAndNegativeEmojis() {
        List<Tweet> tweets = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.TWEET_BELONGS_TO,
                TypeEnum.GROTESQUE_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Tweet t : tweetRepository.getTextNegative()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    tweets.add(t);
                    break;
                }
            }
        }
        return tweets;
    }


    /**
     * This method calls the getTweetsNegativeTextAndNegativeEmojis() and returns the size of the list returned by
     * @return Integer with the size of the list of Tweets that has negative/very_negative sentiment and negative emojis
     */
    @Override
    public Integer getCountTweetsNegativeTextAndNegativeEmojis() {
        return getTweetsNegativeTextAndNegativeEmojis().size();
    }

    /**
     * This method is similar to the getTweetsNegativeTextAndNegativeEmojis(), with the difference that this method
     * compares the emojis with the list of tweets returned by the method getTweetsNegativeTextAndNegativeImage() instead
     * of getTextNegative()
     * @return The list of tweets that contains negative text, negative emojis and negative images
     */
    @Override
    public List<Tweet> getFullNegativeMatchesTweets() {
        List<Tweet> tweets = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.TWEET_BELONGS_TO,
                TypeEnum.GROTESQUE_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Tweet t : tweetRepository.getTextImageNegative()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    tweets.add(t);
                    break;
                }
            }
        }
        return tweets;
    }

    /**
     * This method calls the getFullNegativeMatchesTweets() and returns the size of the list returned by
     * @return Integer with the size of the list of Tweets that contains negative text, negative emojis and negative images
     */
    @Override
    public Integer getCountFullNegativeMatchesTweets() {
        return getFullNegativeMatchesTweets().size();
    }


}

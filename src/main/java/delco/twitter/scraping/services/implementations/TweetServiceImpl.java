package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.utils.DatumConverters;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Override
    public Set<String> getAllEmojisFromTweets(Tweet t){
        Set<String> emojisList = new HashSet<>(new HashSet<>(wordService.getAllEmojisFromText(t.getText())));
        for(Reply r : t.getReplies()){
            emojisList.addAll(new HashSet<>(wordService.getAllEmojisFromText(r.getText())));
        }
        return emojisList;
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

    @Override
    public List<Tweet> findAllTweets() {
        return tweetRepository.findAll();
    }


    // =============================================
    //           FIND POSITIVE CONTENT
    // =============================================


    @Override
    public List<Tweet> getTweetsPositiveTextAndPositiveImage() {
       return tweetRepository.getTextImagePositive();
    }

    @Override
    public Integer getCountTweetsPositiveTextAndPositiveImage() {
        return tweetRepository.getTextImagePositive().size();
    }

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

    @Override
    public Integer getCountTweetsPositiveTextAndPositiveEmojis() {
        return getTweetsPositiveTextAndPositiveEmojis().size();
    }

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


    @Override
    public Integer getCountFullMatchesTweets() {
        return getFullMatchesTweets().size();
    }




    // =============================================
    //           FIND NEGATIVE CONTENT
    // =============================================

    @Override
    public List<Tweet> getTweetsNegativeTextAndNegativeImage() {
        return tweetRepository.getTextImageNegative();
    }

    @Override
    public Integer getCountTweetsNegativeTextAndNegativeImage() {
        return tweetRepository.getTextImageNegative().size();
    }

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

    @Override
    public Integer getCountTweetsNegativeTextAndNegativeEmojis() {
        return getTweetsNegativeTextAndNegativeEmojis().size();
    }

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

    @Override
    public Integer getCountFullNegativeMatchesTweets() {
        return getFullNegativeMatchesTweets().size();
    }


}

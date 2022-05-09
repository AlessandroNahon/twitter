package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.utils.DatumConverters;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.RepliesRepository;
import delco.twitter.scraping.services.interfaces.ImageService;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.SentimentService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class RepliesServiceImpl extends Thread implements RepliesService {

    @Autowired
    private RepliesRepository repliesRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private WordService wordService;


    @Autowired
    private DatumConverters datumConverters;

    private final String belongs_to_reply = "Reply";

    public RepliesServiceImpl() {}

    /**
     * This method is used find all the replies gathered from an original tweet, passing the Id of the original tweet
     * @param tweetId Id of the original tweet
     * @return Set of replies
     */
    @Override
    public Iterable<Reply> findByTweetId(Long tweetId) {
        Set<Reply> replies = new HashSet<>();
        repliesRepository.findAll().forEach(reply -> {
           if(reply.getOriginalTweet().getId().equals(tweetId)) {
               replies.add(reply);
           }
        });
        return replies;
    }

    /**
     * This method is used to parse the Root element from the Twitter API, and set the replies to an original tweet
     * @param root Root element from the Twitter API
     * @param originalTweet Original tweet
     */
    @Override
    public synchronized void parseReplyFromTweet(Root root, Tweet originalTweet) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (root.getData().size() != 0) {
            root.getData().forEach(datum -> {
                Reply reply = datumConverters.convertDatumToReply(datum);
                reply.setOriginalTweet(originalTweet);
                repliesRepository.save(reply);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wordService.analyzeText(reply.getText(),WordServiceImpl.REPLY_BELONGS_TO);
                    }
                }).start();
                if(!datum.getAttachments().getMedia_keys().isEmpty()) {
                    List<Images> images = imageService.getImages(root.getIncludes(),datum);
                    if(!images.isEmpty()) {
                        images.forEach(img -> {
                            img.setReply(reply);
                            imageService.addLabelsAndSaveImage(img);
                        });
                    }
                }
            });
        } else {
            System.out.println("No hay respuestas");
        }
    }

    @Override
    public List<Reply> findAllReplies() {
        return repliesRepository.findAll();
    }

    @Override
    public void deleteByTweetId(Long id) {
        repliesRepository.deleteAllByOriginalTweet(id);
    }

    /**
     * This method calls the repository in order to find all the replies that contains a String passed via
     * parameter
     * @param text The text to search into the replies objects
     * @return The list of replies which contains that text
     */
    @Override
    public List<Reply> findAllByTextContaining(String text) {
        return repliesRepository.findAllByTextContaining(text);
    }

    @Override
    public List<Reply> getReplyPositiveTextAndPositiveImage() {
        return repliesRepository.getTextImagePositive();
    }

    @Override
    public Integer getCountReplyPositiveTextAndPositiveImage() {
        return repliesRepository.getTextImagePositive().size();
    }

    @Override
    public List<Reply> getReplyPositiveTextAndPositiveEmojis() {
        List<Reply> replies = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.REPLY_BELONGS_TO,
                TypeEnum.KITSCH_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Reply t : repliesRepository.getTextPositive()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    replies.add(t);
                    break;
                }
            }
        }
        return replies;
    }

    @Override
    public Integer getCountReplysPositiveTextAndPositiveEmojis() {
        return getReplyPositiveTextAndPositiveEmojis().size();
    }

    @Override
    public List<Reply> getFullMatchesReply() {
        List<Reply> replies = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.REPLY_BELONGS_TO,
                TypeEnum.KITSCH_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Reply t : repliesRepository.getTextImagePositive()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    replies.add(t);
                    break;
                }
            }
        }
        return replies;
    }



    @Override
    public Integer getCountFullMatchesReply() {
        return getFullMatchesReply().size();
    }

    // =============================================
    //           FIND NEGATIVE CONTENT
    // =============================================

    @Override
    public List<Reply> getReplyNegativeTextAndNegativeImage() {
        return repliesRepository.getTextImageNegative();
    }

    @Override
    public Integer getCountReplyNegativeTextAndNegativeImage() {
        return getReplyNegativeTextAndNegativeImage().size();
    }

    @Override
    public List<Reply> getReplyNegativeTextAndNegativeEmojis() {
        List<Reply> replies = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.REPLY_BELONGS_TO,
                TypeEnum.GROTESQUE_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Reply t : repliesRepository.getTextNegative()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    replies.add(t);
                    break;
                }
            }
        }
        return replies;
    }

    @Override
    public Integer getCountReplysNegativeTextAndNegativeEmojis() {
        return getReplyNegativeTextAndNegativeEmojis().size();
    }

    @Override
    public List<Reply> getFullNegativeMatchesReply() {
        List<Reply> replies = new ArrayList<>();
        List<String> emojis = wordService.getAllWordsByBelongsToAndSyntax(WordServiceImpl.REPLY_BELONGS_TO,
                TypeEnum.GROTESQUE_EMOJI).stream().map(Word::getWord).collect(Collectors.toList());
        for(Reply t : repliesRepository.getTextImageNegative()){
            for(String s : wordService.getAllEmojisFromText(t.getText())){
                if(emojis.contains(s)){
                    replies.add(t);
                    break;
                }
            }
        }
        return replies;
    }

    @Override
    public Integer getCountFullNegativeMatchesReply() {
        return getFullNegativeMatchesReply().size();
    }


}

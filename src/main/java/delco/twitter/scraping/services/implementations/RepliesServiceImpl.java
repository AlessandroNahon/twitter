package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
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
                Reply reply = datumConverters.convertDatumToReply(datum, originalTweet.getUsername());
                reply.setOriginalTweet(originalTweet);
                repliesRepository.save(reply);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wordService.analyzeText(reply.getText(),WordServiceImpl.REPLY_BELONGS_TO, originalTweet.getUsername());
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
    public List<Reply> getByOrganization(String organization) {
        return repliesRepository.findByOrganization(organization);
    }

    @Override
    public void deleteByTweetId(Long id) {
        repliesRepository.deleteAllByOriginalTweet(id);
    }

    @Override
    public List<Reply> getBySentiment(String organization, Boolean wantPositive) {
        return repliesRepository.findByOrganizationAndTextSentiment(organization, wantPositive ? SentimentEnum.POSITIVE
                : SentimentEnum.NEGATIVE);
    }


    @Override
    public List<Reply> findTextImage(String username, boolean wantPositive) {
        List<Reply> repliesList = new ArrayList<>();
        for(Reply r : (wantPositive ? repliesRepository.getTextImagePositive(username)
                : repliesRepository.getTextImageNegative(username))){
            if(compareWithThesaurus(r,wantPositive)){
                repliesList.add(r);
            }
        }
        return repliesList;
    }

    private boolean compareWithThesaurus(Reply reply, boolean wantPositive){
        return wantPositive ? wordService.textContainsKistch(reply.getText())
                : wordService.textContainsGrotesque(reply.getText());
    }

    @Override
    public List<Reply> findText(String username, boolean wantPositive) {
        List<Reply> repliesList = new ArrayList<>();
        for(Reply r : (wantPositive ? repliesRepository.getTextPositive(username)
                    : repliesRepository.getTextNegative(username))){
            if(compareWithThesaurus(r,wantPositive)){
                repliesList.add(r);
            }
        }
        return repliesList;
    }

    @Override
    public List<Reply> findTextEmoji(String username, boolean wantPositive) {
        List<Reply> replyList = wantPositive ? findText(username,true) : findText(username,false);
        List<String> tweetListEmoji = wantPositive ?
                wordService.getByBelongsToAndOrganizationBySyntax("Reply",TypeEnum.KITSCH_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList()) :
                wordService.getByBelongsToAndOrganizationBySyntax("Reply",TypeEnum.GROTESQUE_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList());
        Set<Reply> replyListEmojiFiltered = new HashSet<>();
        for(Reply r : replyList){
            List<String> emojisFromTweet = wordService.getAllEmojisFromText(r.getText());
            for(String s : emojisFromTweet){
                if(tweetListEmoji.contains(s)){
                    replyListEmojiFiltered.add(r);
                    break;
                }
            }
        }
        return new ArrayList<>(replyListEmojiFiltered);
    }

    @Override
    public List<Reply> findFullMatches(String username, boolean wantPositive) {
        List<Reply> replyList = wantPositive ? findTextImage(username,true) : findTextImage(username,false);
        List<String> tweetListEmoji = wantPositive ?
                wordService.getByBelongsToAndOrganizationBySyntax("Reply",TypeEnum.KITSCH_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList()) :
                wordService.getByBelongsToAndOrganizationBySyntax("Reply",TypeEnum.GROTESQUE_EMOJI,username)
                        .stream().map(Word::getWord).collect(Collectors.toList());
        Set<Reply> replyListEmojiFiltered = new HashSet<>();
        for(Reply r : replyList){
            List<String> emojisFromTweet = wordService.getAllEmojisFromText(r.getText());
            for(String s : emojisFromTweet){
                if(tweetListEmoji.contains(s)){
                    replyListEmojiFiltered.add(r);
                    break;
                }
            }
        }
        return new ArrayList<>(replyListEmojiFiltered);
    }

    @Override
    public List<Reply> findAllOthers(String username) {
        List<Reply> repliesList = repliesRepository.findByOrganization(username);
        repliesList.removeAll(findTextImage(username,true));
        repliesList.removeAll(findTextEmoji(username,true));
        repliesList.removeAll(findFullMatches(username,true));
        repliesList.removeAll(findTextImage(username,false));
        repliesList.removeAll(findTextEmoji(username,false));
        repliesList.removeAll(findFullMatches(username,false));
        repliesList.removeAll(findText(username,true));
        repliesList.removeAll(findText(username,false));
        return repliesList;
    }

    @Override
    public List<Reply> getCountBySentiment(String username, boolean wantPositive) {
        Set<Reply> replySet = new HashSet<>();
        replySet.addAll(findTextEmoji(username,wantPositive));
        replySet.addAll(findTextImage(username, wantPositive));
        replySet.addAll(findFullMatches(username, wantPositive));
        replySet.addAll(findText(username, wantPositive));
        return new ArrayList<>(replySet);
    }

    @Override
    public List<Reply> findByText(String text, String organization) {
        return repliesRepository.findAllByTextContaining(text, organization);
    }


}

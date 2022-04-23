package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.repositories.RepliesRepository;
import delco.twitter.scraping.services.interfaces.RepliesService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class RepliesServiceImpl implements RepliesService {

    private final RepliesRepository repliesRepository;

    public RepliesServiceImpl(RepliesRepository repliesRepository) {
        this.repliesRepository = repliesRepository;
    }

    @Override
    public Reply saveTweet(Reply tweet) {
        return repliesRepository.save(tweet);
    }

    @Override
    public Iterable<Reply> saveTweets(Iterable<Reply> tweets) {
        return repliesRepository.saveAll(tweets);
    }

    @Override
    public Iterable<Reply> findAll() {
        return repliesRepository.findAll();
    }

    @Override
    public Reply findById(Long id) {
        return repliesRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Reply tweet) {
        repliesRepository.delete(tweet);
    }

    @Override
    public void deleteTweets() {
        repliesRepository.deleteAll();
    }

    @Override
    public void deleteTweets(Iterable<Reply> tweets) {
        repliesRepository.deleteAll(tweets);
    }

    @Override
    public Reply deleteById(Long id) {
        return repliesRepository.findById(id).orElse(null);
    }

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
}

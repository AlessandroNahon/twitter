package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.TweetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;

    public TweetServiceImpl(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }


    @Override
    public Tweet saveTweet(Tweet tweet) {
        return tweetRepository.save(tweet);
    }

    @Override
    public Iterable<Tweet> saveTweets(Iterable<Tweet> tweets) {
        return tweetRepository.saveAll(tweets);
    }

    @Override
    public Iterable<Tweet> findAll() {
        return tweetRepository.findAll();
    }

    @Override
    public Tweet findById(Long id) {
        return tweetRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Tweet tweet) {
        tweetRepository.delete(tweet);
    }

    @Override
    public void deleteTweets() {
        tweetRepository.deleteAll();
    }

    @Override
    public void deleteTweets(Iterable<Tweet> tweets) {
        tweetRepository.deleteAll(tweets);
    }

    @Override
    public Tweet deleteById(Long id) {
        return tweetRepository.findById(id).map(tweet -> {tweetRepository.delete(tweet); return tweet;}).orElse(null);
    }


}

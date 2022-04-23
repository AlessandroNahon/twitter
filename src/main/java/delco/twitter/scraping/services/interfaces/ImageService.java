package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Image;

public interface ImageService {

    Image saveTweet(Image tweet);

    Iterable<Image> saveTweets(Iterable<Image> tweets);

    Iterable<Image> findAll();

    Image findById(Long id);

    void delete(Image tweet);

    void deleteTweets();

    void deleteTweets(Iterable<Image> tweets);

    Image deleteById(Long id);
}

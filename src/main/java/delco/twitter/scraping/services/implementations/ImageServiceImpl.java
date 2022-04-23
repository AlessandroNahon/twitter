package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Image;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.services.interfaces.ImageService;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image saveTweet(Image tweet) {
        return imageRepository.save(tweet);
    }

    @Override
    public Iterable<Image> saveTweets(Iterable<Image> tweets) {
        return imageRepository.saveAll(tweets);
    }

    @Override
    public Iterable<Image> findAll() {
        return imageRepository.findAll();
    }

    @Override
    public Image findById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Image tweet) {
        imageRepository.delete(tweet);
    }

    @Override
    public void deleteTweets() {
        imageRepository.deleteAll();
    }

    @Override
    public void deleteTweets(Iterable<Image> tweets) {
        imageRepository.deleteAll(tweets);
    }

    @Override
    public Image deleteById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }
}

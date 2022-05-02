package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.services.implementations.*;
import delco.twitter.scraping.services.interfaces.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration

@ComponentScan(basePackageClasses = Configuration.class)
public class Configuration {

    private final String BEARER_TOKEN;

    public Configuration(@Value("${BEARER_TOKEN}") String BEARER_TOKEN){
        this.BEARER_TOKEN = BEARER_TOKEN;
    }

    @Bean
    public VisionAPIService visionAPIService(){
        return new VisionAPIServiceImpl();
    }

    @Bean
    public WordService wordService(){
        return new WordServiceImpl();
    }

    @Bean
    public TwitterAPIService twitterAPIService(){
        return new TwitterAPIServiceImpl(BEARER_TOKEN);
    }

    @Bean
    public SentimentService sentimentService(){
        return new SentimentServiceImpl();
    }

    @Bean
    public RepliesService repliesService(){
        return new RepliesServiceImpl();
    }

    @Bean
    public ImageService imageService(){
        return new ImageServiceImpl();
    }
}

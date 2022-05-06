package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.services.implementations.*;
import delco.twitter.scraping.services.interfaces.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@org.springframework.context.annotation.Configuration
@ComponentScan(basePackageClasses = Configuration.class)
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "token")
public class Configuration {

    @Bean
    public VisionAPIService visionAPIService(){
        System.out.println();
        return new VisionAPIServiceImpl();
    }

    @Bean
    public WordService wordService(){
        return new WordServiceImpl();
    }

    @Bean
    public TwitterAPIService twitterAPIService(){
        return new TwitterAPIServiceImpl();
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

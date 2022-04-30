package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.SentimentEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

@Data
@EqualsAndHashCode(exclude = {"originalTweet","image"})
@Entity
@Table(name = "responses")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String text;

    @Enumerated(EnumType.STRING)
    private SentimentEnum textSentiment;
    //private Set<String> images_urls = new HashSet<String>();


    @ManyToOne
    @JoinColumn(name = "original_tweet_id")
    private Tweet originalTweet;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL)
    private Set<Images> images = new HashSet<>();

    public Reply addImage(Images images){
        if(this.images == null){
            this.images = new HashSet<>();
        }
        this.images.add(images);
        images.setReply(this);
        return this;
    }

    public Reply remove(Images images){
        this.images.remove(images);
        return this;
    }





}

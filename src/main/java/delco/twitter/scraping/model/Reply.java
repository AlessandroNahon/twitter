package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.ContentEnum;
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

    @Enumerated(EnumType.STRING)
    private ContentEnum imageContent;

    @ManyToOne
    @JoinColumn(name = "original_tweet_id")
    private Tweet originalTweet;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL)
    private Set<Image> image = new HashSet<>();

    public Reply addImage(Image image){
        this.image.add(image);
        image.setReply(this);
        return this;
    }

    public Reply remove(Image image){
        this.image.remove(image);
        return this;
    }





}

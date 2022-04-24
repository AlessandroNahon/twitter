package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.ContentEnum;
import delco.twitter.scraping.model.enumerations.SentimentEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

@Entity(name="tweet")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Tweet.findLastTweet",
                query = "SELECT id,conversation_id,created_at,image_content,text," +
                        "text_sentiment,username FROM tweets t ORDER BY t.id DESC",
                resultClass = Tweet.class)
})
@Data
@EqualsAndHashCode(exclude = {"replies","image"})
@Table(name = "tweets")
public class Tweet{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String text;
    private String username;

    @Enumerated(EnumType.STRING)
    private SentimentEnum textSentiment;

    @Enumerated(EnumType.STRING)
    private ContentEnum imageContent;
    private Date createdAt;
    private String conversationId;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "originalTweet")
    private Set<Reply> replies = new HashSet<>();

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Image> image = new HashSet<>();

    public Tweet addReply(Reply reply){
        this.replies.add(reply);
        reply.setOriginalTweet(this);
        return this;
    }

    public Tweet removeReply(Reply reply){
        this.replies.remove(reply);
        return this;
    }

    public Tweet addImage(Image image){
        this.image.add(image);
        image.setTweet(this);
        return this;
    }

    public Tweet removeImage(Image image){
        this.image.remove(image);
        return this;
    }


}

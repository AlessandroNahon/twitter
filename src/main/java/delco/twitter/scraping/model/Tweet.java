package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.SentimentEnum;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity(name="tweet")

@Data
@EqualsAndHashCode(exclude = {"replies","image"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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


    private boolean possibly_sensitive;


    private Date createdAt;
    private String conversationId;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "originalTweet")
    private Set<Reply> replies = new HashSet<>();

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Images> images = new HashSet<>();

    public void addReply(Reply reply){
        if(this.replies == null){
            this.replies = new HashSet<>();
        }
        this.replies.add(reply);
        reply.setOriginalTweet(this);
    }

    public Tweet removeReply(Reply reply){
        this.replies.remove(reply);
        return this;
    }

    public Tweet addImage(Images images){
        if(this.images == null){
            this.images = new HashSet<>();
        }
        this.images.add(images);
        images.setTweet(this);
        return this;
    }

    public Tweet removeImage(Images images){
        this.images.remove(images);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        return id != null ? id.equals(tweet.id) : tweet.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", username='" + username + '\'' +
                ", textSentiment=" + textSentiment +
                ", createdAt=" + createdAt +
                ", conversationId='" + conversationId;
    }
}

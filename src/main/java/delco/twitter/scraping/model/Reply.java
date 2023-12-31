package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.SentimentEnum;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Data
@EqualsAndHashCode(exclude = {"originalTweet","image"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "responses")
public class Reply  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String text;

    @Enumerated(EnumType.STRING)
    private SentimentEnum textSentiment;
    //private Set<String> images_urls = new HashSet<String>();


    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "original_tweet_id")
    private Tweet originalTweet;

    private String organization;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL)
    private Set<Images> images = new HashSet<>();

    public void addImage(Images images){
        if(this.images == null){
            this.images = new HashSet<>();
        }
        this.images.add(images);
        images.setReply(this);
    }

    public Reply remove(Images images){
        this.images.remove(images);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reply reply = (Reply) o;

        return id != null ? id.equals(reply.id) : reply.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", textSentiment=" + textSentiment +
                '}';
    }
}

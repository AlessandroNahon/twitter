package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.SentimentEnum;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude = {"originalTweet","image"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "sentiments")
public class Sentiments {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public String sentiment;

    @Column(name = "belongs_to", nullable = false)
    public String belongs_to;
    public String organization;
    public int appearances = 0;



}

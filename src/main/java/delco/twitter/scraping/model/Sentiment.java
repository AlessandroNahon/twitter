package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.SentimentEnum;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Sentiment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public String sentiment;
    public int appearances = 0;



}

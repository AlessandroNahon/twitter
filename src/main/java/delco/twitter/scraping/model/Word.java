package delco.twitter.scraping.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String word;
    private int count;


}

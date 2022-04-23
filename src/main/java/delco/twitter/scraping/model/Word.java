package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.SyntaxEnum;
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

    @Enumerated(EnumType.STRING)
    private SyntaxEnum syntax;

    public Word(String word, int count, SyntaxEnum syntax) {
        this.word = word;
        this.count = count;
        this.syntax = syntax;
    }

    public Word(){}



}

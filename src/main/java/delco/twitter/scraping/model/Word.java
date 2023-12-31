package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.TypeEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "words")
@Table(name = "words")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Word  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String word;
    private int count;

    @Enumerated(EnumType.STRING)
    private TypeEnum syntax;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return id != null ? id.equals(word.id) : word.id == null;
    }

    @Column(name = "belongs_to", nullable = false)
    public String belongsTo;

    @Column(name = "organization", nullable = false)
    public String organization;

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", count=" + count +
                ", syntax=" + syntax +
                '}';
    }
}

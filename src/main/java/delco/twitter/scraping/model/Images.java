package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.TypeEnum;
import lombok.*;

import javax.persistence.*;
import java.util.Arrays;

@Data
@EqualsAndHashCode(exclude = {"tweet","reply"})
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "image")
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] image;

    @ManyToOne
    private Tweet tweet;

    @Enumerated(EnumType.STRING)
    private TypeEnum imageContent;

    @ManyToOne
    private Reply reply;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Images images = (Images) o;

        return id != null ? id.equals(images.id) : images.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


}

package delco.twitter.scraping.model;

import delco.twitter.scraping.model.enumerations.TypeEnum;
import delco.twitter.scraping.model.utils.StringListConverter;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"tweet","reply"})
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "image")
public class Images implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private Tweet tweet;

    @Enumerated(EnumType.STRING)
    private TypeEnum imageContent;

    @Lob
    @Convert(converter = StringListConverter.class)
    private List<String> imageObjects = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private Reply reply;

    public void addImageObject(String object){
        if(imageObjects == null){
            imageObjects = new ArrayList<>();
            imageObjects.add(object);
        }else{
            imageObjects.add(object);
        }
    }

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


    @Override
    public String toString() {
        return "Images{" +
                "id=" + id +
                ", tweet=" + tweet +
                ", imageContent=" + imageContent +
                ", imageObjects=" + imageObjects +
                ", reply=" + reply +
                '}';
    }
}

package delco.twitter.scraping.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude = {"tweet","reply"})
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] image;

    @ManyToOne
    private Tweet tweet;

    @ManyToOne
    private Reply reply;

    public Image(byte[] image){
        this.image = image;
    }

    public Image(){}


}

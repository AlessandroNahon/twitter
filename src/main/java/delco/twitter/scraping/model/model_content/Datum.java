package delco.twitter.scraping.model.model_content;

import lombok.Data;

import java.util.Date;

public @Data class Datum {

    private String conversation_id;
    private String id;
    private Date created_at;
    private String text;
    private Attachments attachments = new Attachments();

}

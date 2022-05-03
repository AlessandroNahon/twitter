package delco.twitter.scraping.model.twitterapi.model_content;

import lombok.Data;

import java.util.Date;

public @Data class Datum {

    public Date created_at;
    public boolean possibly_sensitive;
    public String text;
    public String id;
    public Attachments attachments = new Attachments();
    public String conversation_id;

}

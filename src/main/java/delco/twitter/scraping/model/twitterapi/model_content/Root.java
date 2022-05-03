package delco.twitter.scraping.model.twitterapi.model_content;

import lombok.Data;

import java.util.ArrayList;

public @Data class Root {

    public ArrayList<Datum> data = new ArrayList<>();
    public Includes includes;
    public Meta meta;


}

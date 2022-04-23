package delco.twitter.scraping.model.model_content;

import lombok.Data;

import java.util.ArrayList;

public @Data class Root {

    public ArrayList<Datum> data = new ArrayList<>();
    public Includes includes = new Includes();
    public Meta meta = new Meta();


}

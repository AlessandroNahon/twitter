package delco.twitter.scraping.model.twitterapi.model_content;

import lombok.Data;

public @Data
class Meta {

    public String newest_id;
    public String oldest_id;
    public int result_count;
    public String next_token;

}

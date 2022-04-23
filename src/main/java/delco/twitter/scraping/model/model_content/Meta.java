package delco.twitter.scraping.model.model_content;

import lombok.Data;

public @Data
class Meta {
    public String oldest_id;
    public String newest_id;
    public int result_count;
    public String next_token;

}

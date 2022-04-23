package delco.twitter.scraping.services.interfaces;

import delco.twitter.scraping.model.Word;

public interface WordService {

    void save(Word word);

    void delete(Word word);



}

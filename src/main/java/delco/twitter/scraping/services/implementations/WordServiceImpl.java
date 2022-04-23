package delco.twitter.scraping.services.implementations;

import delco.twitter.scraping.model.Word;
import delco.twitter.scraping.repositories.ThesaurusRepository;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.stereotype.Service;

@Service
public class WordServiceImpl implements WordService {

    private final ThesaurusRepository thesaurusRepository;

    public WordServiceImpl(ThesaurusRepository thesaurusRepository) {
        this.thesaurusRepository = thesaurusRepository;
    }

    @Override
    public void save(Word word) {
        thesaurusRepository.save(word);
    }

    @Override
    public void delete(Word word) {
        thesaurusRepository.delete(word);
    }
}

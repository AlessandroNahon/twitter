package delco.twitter.scraping.services.pipelinenlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class Pipeline {

    /**
     * This class is used to initialize the StanfordNLP pipeline, in order to be able to analyze each individual
     * word later in the WordServiceImpl
     */

    private static Properties properties;
    private static String propertiesName = "";
    private static StanfordCoreNLP stanfordCoreNLP;

    private Pipeline() { }

    static {
        properties = new Properties();
        properties.setProperty("annotators", propertiesName);
    }

    public static StanfordCoreNLP getPipeline() {
        if(stanfordCoreNLP == null) {
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }
        return stanfordCoreNLP;
    }
}
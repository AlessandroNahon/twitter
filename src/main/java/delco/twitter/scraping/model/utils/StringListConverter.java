package delco.twitter.scraping.model.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return attribute != null ? String.join(SPLIT_CHAR, attribute) : "";
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData != null ? Arrays.asList(dbData.split(SPLIT_CHAR)) : emptyList();
    }

}

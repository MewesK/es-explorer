package net.mewk.ese.mapper.es;

import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.server.Field;
import net.mewk.ese.model.server.MetaData;
import net.mewk.ese.model.server.NestedType;
import net.mewk.ese.model.server.SimpleField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class FieldMapper implements Mapper<Map.Entry<String, Object>, Field> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    @SuppressWarnings("unchecked")
    public Field map(Map.Entry<String, Object> object) {
        // Create instance
        Field field = new SimpleField(object.getKey());

        // Set data
        if (object.getValue() instanceof Map) {
            for (Map.Entry<String, Object> fieldSetEntry : ((Map<String, Object>) object.getValue()).entrySet()) {
                String key = fieldSetEntry.getKey();
                Object value = fieldSetEntry.getValue();

                if (key == null || value == null) {
                    continue;
                }

                // Basic type
                if (key.equals("type")) {
                    if (field instanceof SimpleField && value instanceof String) {
                        ((SimpleField) field).setType((String) value);
                    }
                }

                // Nested type
                if (key.equals("properties")) {
                    if (fieldSetEntry.getValue() instanceof Map) {
                        // Create instance
                        NestedType nestedType = new NestedType(object.getKey());

                        // Set data
                        for (Map.Entry<String, Object> nestedFieldSetEntry : ((Map<String, Object>) fieldSetEntry.getValue()).entrySet()) {
                            Field nestedField = map(nestedFieldSetEntry);
                            nestedType.getFieldMap().put(nestedField.getName(), nestedField);
                        }

                        // Override instance
                        field = nestedType;
                    }
                }

                // Add meta data
                field.getMetaDataList().add(new MetaData(key, value));
            }
        } else {
            logger.error("Error: Field value should always be a map");
        }

        return field;
    }
}

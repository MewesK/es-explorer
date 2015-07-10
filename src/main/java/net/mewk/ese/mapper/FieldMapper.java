package net.mewk.ese.mapper;

import net.mewk.ese.Main;
import net.mewk.ese.model.Field;
import net.mewk.ese.model.MetaData;
import net.mewk.ese.model.NestedType;
import net.mewk.ese.model.SimpleField;

import java.util.Map;
import java.util.Set;

public class FieldMapper implements Mapper<Map.Entry<String, Map>, Field> {

    @Override
    public Field map(Map.Entry<String, Map> object) {
        // Create instance
        Field field = new SimpleField(object.getKey());

        // Set data
        Set<Map.Entry<String, Object>> stringObjectSet = object.getValue().entrySet();
        for (Map.Entry<String, Object> stringObjectEntry : stringObjectSet) {
            // Basic type
            if (stringObjectEntry.getKey().equals("type")) {
                ((SimpleField) field).setType((String) stringObjectEntry.getValue());
            }

            // Nested type
            if (stringObjectEntry.getKey().equals("properties")) {
                // Create instance
                NestedType nestedType = new NestedType(object.getKey());

                // Set data
                Set<Map.Entry<String, Object>> nestedStringObjectSet = ((Map<String, Object>) stringObjectEntry.getValue()).entrySet();
                for (Map.Entry<String, Object> nestedStringObjectEntry : nestedStringObjectSet) {
                    Field nestedField = (Field) Main.getMapperManager().findByClass(Field.class).map(nestedStringObjectEntry);
                    nestedType.getFieldMap().put(nestedField.getName(), nestedField);
                }

                // Override instance
                field = nestedType;
            }

            // Add meta data
            field.getMetaDataList().add(new MetaData(stringObjectEntry.getKey(), stringObjectEntry.getValue()));
        }

        return field;
    }
}

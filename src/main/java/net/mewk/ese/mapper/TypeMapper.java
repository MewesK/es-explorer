package net.mewk.ese.mapper;

import net.mewk.ese.Main;
import net.mewk.ese.model.Field;
import net.mewk.ese.model.MetaData;
import net.mewk.ese.model.SimpleType;
import net.mewk.ese.model.Type;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class TypeMapper implements Mapper<MappingMetaData, Type> {

    @Override
    public Type map(MappingMetaData object) {
        // Create instance
        Type type = new SimpleType(object.type());

        // Set data
        try {
            Set<Map.Entry<String, Object>> stringObjectSet = object.sourceAsMap().entrySet();
            for (Map.Entry<String, Object> stringObjectEntry : stringObjectSet) {
                if (stringObjectEntry.getKey().equals("properties")) {
                    Set<Map.Entry<String, Map>> stringMapSet = ((Map<String, Map>) stringObjectEntry.getValue()).entrySet();
                    for (Map.Entry<String, Map> stringMapEntry : stringMapSet) {
                        Field field = (Field) Main.getMapperManager().findByClass(Field.class).map(stringMapEntry);
                        type.getFieldMap().put(field.getName(), field);
                    }
                }

                // Add meta data
                type.getMetaDataList().add(new MetaData(stringObjectEntry.getKey(), stringObjectEntry.getValue()));
            }
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }

        return type;
    }
}

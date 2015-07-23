package net.mewk.ese.mapper.es;

import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.server.Field;
import net.mewk.ese.model.server.MetaData;
import net.mewk.ese.model.server.SimpleType;
import net.mewk.ese.model.server.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;

@Singleton
public class TypeMapper implements Mapper<MappingMetaData, Type> {

    private static final Logger LOG = LogManager.getLogger(TypeMapper.class);

    @Inject
    FieldMapper fieldMapper;

    @Override
    @SuppressWarnings("unchecked")
    public Type map(MappingMetaData object) {
        // Create instance
        Type type = new SimpleType(object.type());

        // Set data
        try {
            for (Map.Entry<String, Object> typeSetEntry : object.sourceAsMap().entrySet()) {
                if (typeSetEntry.getKey().equals("properties")) {
                    if (typeSetEntry.getValue() instanceof Map) {
                        for (Map.Entry<String, Object> fieldSetEntry : ((Map<String, Object>) typeSetEntry.getValue()).entrySet()) {
                            Field field = fieldMapper.map(fieldSetEntry);
                            type.getFieldMap().put(field.getName(), field);
                        }
                    }
                }

                // Add meta data
                type.getMetaDataList().add(new MetaData(typeSetEntry.getKey(), typeSetEntry.getValue()));
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return type;
    }
}

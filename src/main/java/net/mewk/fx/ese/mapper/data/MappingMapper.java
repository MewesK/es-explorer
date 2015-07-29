package net.mewk.fx.ese.mapper.data;

import net.mewk.fx.ese.mapper.Mapper;
import net.mewk.fx.ese.model.mapping.*;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.hppc.ObjectContainer;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;

@Singleton
public class MappingMapper implements Mapper<ObjectContainer<IndexMetaData>, Mapping> {

    @Override
    public Mapping map(ObjectContainer<IndexMetaData> object) throws IOException {
        // Create mapping
        Mapping mapping = new Mapping();

        // TODO: Fix pretty printer to use only one line break (windows)
        XContentBuilder contentBuilder = JsonXContent.contentBuilder().prettyPrint().lfAtEnd();
        contentBuilder.startObject();

        // Set indices
        for (ObjectCursor<IndexMetaData> indexMetaDataObjectCursor : object) {
            // Build raw
            contentBuilder.startObject(indexMetaDataObjectCursor.value.getIndex(), XContentBuilder.FieldCaseConversion.NONE);
            contentBuilder.startObject("mappings");

            // Create index
            Index index = new Index(indexMetaDataObjectCursor.value.index());

            // Set types
            ObjectContainer<MappingMetaData> mappingMetaDataObjectContainer = indexMetaDataObjectCursor.value.mappings().values();
            for (ObjectCursor<MappingMetaData> mappingMetaDataObjectCursor : mappingMetaDataObjectContainer) {
                contentBuilder.field(mappingMetaDataObjectCursor.value.type());
                contentBuilder.map(mappingMetaDataObjectCursor.value.sourceAsMap());

                Type type = mapToType(mappingMetaDataObjectCursor.value);
                index.getTypeMap().put(type.getName(), type);
            }

            // Add to mapping
            mapping.getIndices().put(index.getName(), index);

            contentBuilder.endObject();
            contentBuilder.endObject();
        }

        // Set raw
        contentBuilder.endObject();
        mapping.setRaw(contentBuilder.string());

        return mapping;
    }

    @SuppressWarnings("unchecked")
    private Type mapToType(MappingMetaData object) throws IOException {
        // Create type
        Type type = new SimpleType(object.type());

        // Add fields
        for (Map.Entry<String, Object> typeSetEntry : object.sourceAsMap().entrySet()) {
            if (typeSetEntry.getKey().equals("properties")) {
                if (typeSetEntry.getValue() instanceof Map) {
                    for (Map.Entry<String, Object> fieldSetEntry : ((Map<String, Object>) typeSetEntry.getValue()).entrySet()) {
                        Field field = mapToField(fieldSetEntry);
                        type.getFieldMap().put(field.getName(), field);
                    }
                }
            }

            // Add meta data
            type.getMetaDataList().add(new MetaData(typeSetEntry.getKey(), typeSetEntry.getValue()));
        }

        return type;
    }

    @SuppressWarnings("unchecked")
    private Field mapToField(Map.Entry<String, Object> object) {
        // Create field
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
                else if (key.equals("properties")) {
                    if (fieldSetEntry.getValue() instanceof Map) {
                        // Create instance
                        NestedType nestedType = new NestedType(object.getKey());

                        // Set data
                        for (Map.Entry<String, Object> nestedFieldSetEntry : ((Map<String, Object>) fieldSetEntry.getValue()).entrySet()) {
                            Field nestedField = mapToField(nestedFieldSetEntry);
                            nestedType.getFieldMap().put(nestedField.getName(), nestedField);
                        }

                        // Override instance
                        field = nestedType;
                    }
                }

                // Add meta data
                field.getMetaDataList().add(new MetaData(key, value));
            }
        }

        return field;
    }
}

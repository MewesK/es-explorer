package net.mewk.ese.mapper;

import net.mewk.ese.Main;
import net.mewk.ese.model.Index;
import net.mewk.ese.model.Type;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.hppc.ObjectContainer;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;

public class IndexMapper implements Mapper<IndexMetaData, Index> {

    @Override
    public Index map(IndexMetaData object) {
        // Create instance
        Index index = new Index(object.index());

        // Set data
        ObjectContainer<MappingMetaData> mappingMetaDataObjectContainer = object.mappings().values();
        for (ObjectCursor<MappingMetaData> mappingMetaDataObjectCursor : mappingMetaDataObjectContainer) {
            Type type = (Type) Main.getMapperManager().findByClass(Type.class).map(mappingMetaDataObjectCursor.value);
            index.getTypeMap().put(type.getName(), type);
        }

        return index;
    }
}

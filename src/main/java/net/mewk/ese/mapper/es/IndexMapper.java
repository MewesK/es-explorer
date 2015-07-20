package net.mewk.ese.mapper.es;

import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.server.Index;
import net.mewk.ese.model.server.Type;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.hppc.ObjectContainer;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IndexMapper implements Mapper<IndexMetaData, Index> {

    @Inject
    TypeMapper typeMapper;

    @Override
    public Index map(IndexMetaData object) {
        // Create instance
        Index index = new Index(object.index());

        // Set data
        ObjectContainer<MappingMetaData> mappingMetaDataObjectContainer = object.mappings().values();
        for (ObjectCursor<MappingMetaData> mappingMetaDataObjectCursor : mappingMetaDataObjectContainer) {
            Type type = typeMapper.map(mappingMetaDataObjectCursor.value);
            index.getTypeMap().put(type.getName(), type);
        }

        return index;
    }
}

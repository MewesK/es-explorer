package net.mewk.ese.manager;

import com.google.common.collect.Maps;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.mapper.es.FieldMapper;
import net.mewk.ese.mapper.es.IndexMapper;
import net.mewk.ese.mapper.es.TypeMapper;
import net.mewk.ese.mapper.ui.IndexViewMapper;
import net.mewk.ese.mapper.ui.ResultViewMapper;

import java.util.Map;

public class MapperManager {

    private Map<Class, Mapper> mapperList = Maps.newHashMap();

    public MapperManager() {
        mapperList.put(IndexMapper.class, new IndexMapper());
        mapperList.put(FieldMapper.class, new FieldMapper());
        mapperList.put(TypeMapper.class, new TypeMapper());
        mapperList.put(IndexViewMapper.class, new IndexViewMapper());
        mapperList.put(ResultViewMapper.class, new ResultViewMapper());
    }

    public Mapper findByClass(Class<?> clazz) {
        for (Map.Entry<Class, Mapper> mapperEntry : mapperList.entrySet()) {
            if (mapperEntry.getKey().equals(clazz)) {
                return mapperEntry.getValue();
            }
        }
        return null;
    }

    public Map<Class, Mapper> getMapperList() {
        return mapperList;
    }

    public void setMapperList(Map<Class, Mapper> mapperList) {
        this.mapperList = mapperList;
    }
}

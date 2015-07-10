package net.mewk.ese.manager;

import com.google.common.collect.Maps;
import net.mewk.ese.mapper.FieldMapper;
import net.mewk.ese.mapper.IndexMapper;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.mapper.TypeMapper;
import net.mewk.ese.model.Field;
import net.mewk.ese.model.Index;
import net.mewk.ese.model.Type;

import java.util.Map;

public class MapperManager {

    private Map<Class, Mapper> mapperList = Maps.newHashMap();

    public MapperManager() {
        mapperList.put(Index.class, new IndexMapper());
        mapperList.put(Field.class, new FieldMapper());
        mapperList.put(Type.class, new TypeMapper());
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

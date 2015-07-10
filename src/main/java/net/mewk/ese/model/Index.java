package net.mewk.ese.model;

import com.google.common.collect.Lists;
import org.elasticsearch.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Index implements MetaDataContainer, Serializable {

    protected String name;
    protected Map<String, Type> typeMap = Maps.newHashMap();
    protected List<MetaData> metaDataList = Lists.newArrayList();

    public Index(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Type> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, Type> typeMap) {
        this.typeMap = typeMap;
    }

    public List<MetaData> getMetaDataList() {
        return metaDataList;
    }

    public void setMetaDataList(List<MetaData> metaDataList) {
        this.metaDataList = metaDataList;
    }
}

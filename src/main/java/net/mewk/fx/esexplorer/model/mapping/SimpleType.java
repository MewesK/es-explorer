package net.mewk.fx.esexplorer.model.mapping;

import com.google.common.collect.Lists;
import org.elasticsearch.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SimpleType implements Type, Serializable {

    private String name;
    private Map<String, Field> fieldMap = Maps.newHashMap();
    private List<MetaData> metaDataList = Lists.newArrayList();

    public SimpleType(String name) {
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

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<String, Field> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public List<MetaData> getMetaDataList() {
        return metaDataList;
    }

    public void setMetaDataList(List<MetaData> metaDataList) {
        this.metaDataList = metaDataList;
    }
}

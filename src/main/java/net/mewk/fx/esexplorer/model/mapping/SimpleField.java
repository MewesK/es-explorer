package net.mewk.fx.esexplorer.model.mapping;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class SimpleField implements Field, Serializable {

    private String name;
    private String type;
    private List<MetaData> metaDataList = Lists.newArrayList();

    public SimpleField(String name) {
        this(name, null);
    }

    public SimpleField(String name, String type) {
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MetaData> getMetaDataList() {
        return metaDataList;
    }

    public void setMetaDataList(List<MetaData> metaDataList) {
        this.metaDataList = metaDataList;
    }
}

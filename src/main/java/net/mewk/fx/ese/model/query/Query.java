package net.mewk.fx.ese.model.query;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Query implements Cloneable, Serializable {

    protected File file;
    protected String query;
    protected List<String> indexNameList;

    public Query() {
        this(null, "", Lists.newArrayList());
    }

    public Query(File file, String query) {
        this(file, query, "_all");

    }

    public Query(File file, String query, String index) {
        this(file, query, Lists.newArrayList(index));
    }

    public Query(File file, String query, List<String> indexNameList) {
        this.file = file;
        this.query = query;
        this.indexNameList = indexNameList;
    }

    public Query clone() throws CloneNotSupportedException {
        return (Query) super.clone();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getIndexNameList() {
        return indexNameList;
    }

    public void setIndexNameList(List<String> indexNameList) {
        this.indexNameList = indexNameList;
    }
}

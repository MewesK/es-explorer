package net.mewk.ese.model.query;

import java.io.File;
import java.io.Serializable;

public class Query implements Cloneable, Serializable {

    protected File file;
    protected String query;

    public Query() {
        this(null, "");
    }

    public Query(File file, String query) {
        this.file = file;
        this.query = query;
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
}

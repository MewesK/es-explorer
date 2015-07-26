package net.mewk.ese.model.mapping;

import com.google.common.collect.Maps;
import net.mewk.ese.model.connection.Connection;

import java.io.Serializable;
import java.util.Map;

public class Mapping implements Serializable {

    private Connection connection;
    private Map<String, Index> indices = Maps.newHashMap();
    private String raw = null;

    public Mapping() {
    }

    public Mapping(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Map<String, Index> getIndices() {
        return indices;
    }

    public void setIndices(Map<String, Index> indices) {
        this.indices = indices;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}

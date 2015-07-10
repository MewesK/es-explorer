package net.mewk.ese.model;

public class MetaData {

    private String name;
    private Object value;

    public MetaData(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(SimpleType value) {
        this.value = value;
    }
}

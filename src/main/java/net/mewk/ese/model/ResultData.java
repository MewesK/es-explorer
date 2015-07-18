package net.mewk.ese.model;

public class ResultData {

    private String index;
    private String name;
    private Object value;
    private Float score;

    public ResultData(String index, String type, Object value, Float score) {
        this.index = index;
        this.name = type;
        this.value = value;
        this.score = score;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Float getScore() {
        return score;
    }
}

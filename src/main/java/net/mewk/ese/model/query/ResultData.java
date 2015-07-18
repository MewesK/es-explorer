package net.mewk.ese.model.query;

public class ResultData {

    private final String index;
    private final String name;
    private final Object value;
    private final Float score;

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

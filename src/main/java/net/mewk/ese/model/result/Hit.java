package net.mewk.ese.model.result;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Hit implements Comparable<Float> {

    private final String index;
    private final String name;
    private final Object value;
    private final Float score;

    private final List<Hit> hits = Lists.newArrayList();

    public Hit(String index, String type, Object value, Float score) {
        this.index = index;
        this.name = type;
        this.value = value;
        this.score = score;
    }

    @Override
    public int compareTo(@NotNull Float value) {
        return Float.compare(score, value);
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

    public List<Hit> getHits() {
        return hits;
    }
}

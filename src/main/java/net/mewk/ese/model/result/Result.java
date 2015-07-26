package net.mewk.ese.model.result;

import com.google.common.collect.Lists;

import java.util.List;

public class Result {

    private List<Hit> hits = Lists.newArrayList();
    private String raw = null;

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}

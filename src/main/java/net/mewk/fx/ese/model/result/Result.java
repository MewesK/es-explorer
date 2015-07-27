package net.mewk.fx.ese.model.result;

import com.google.common.collect.Lists;
import net.mewk.fx.ese.model.query.Query;

import java.util.List;

public class Result {

    private Query query;
    private List<Hit> hits = Lists.newArrayList();
    private String raw = null;

    public Result() {
        this(null);
    }

    public Result(Query query) {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

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

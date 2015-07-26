package net.mewk.ese.mapper.data;

import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.result.Hit;
import net.mewk.ese.model.result.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ResultMapper implements Mapper<SearchResponse, Result> {

    @Override
    public Result map(SearchResponse object) {
        // Create result
        Result result = new Result();
        result.setRaw(object.toString());

        // Add hits
        for (SearchHit searchHit : object.getHits().getHits()) {
            // Create hit
            Hit hit = new Hit(searchHit.index(), searchHit.type(), searchHit.sourceAsMap(), searchHit.score());

            // Add sub hits
            for (Map.Entry<String, Object> sourceEntry : searchHit.getSource().entrySet()) {
                hit.getHits().add(mapToHit(searchHit, sourceEntry));
            }

            // Add to result
            result.getHits().add(hit);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Hit mapToHit(SearchHit object, Map.Entry<String, Object> sourceEntry) {
        // Create sub hit
        Hit hit = new Hit(object.index(), sourceEntry.getKey(), sourceEntry.getValue(), null);

        // Add sub hits
        if (sourceEntry.getValue() instanceof Map) {
            for (Map.Entry<String, Object> _sourceEntry : ((Map<String, Object>) sourceEntry.getValue()).entrySet()) {
                hit.getHits().add(mapToHit(object, _sourceEntry));
            }
        } else if (sourceEntry.getValue() instanceof List) {
            int i = 0;
            for (Object _sourceEntry : (List<Object>) sourceEntry.getValue()) {
                hit.getHits().add(mapToHit(object, new AbstractMap.SimpleEntry<>(String.valueOf(i), _sourceEntry)));
                i++;
            }
        }

        return hit;
    }
}

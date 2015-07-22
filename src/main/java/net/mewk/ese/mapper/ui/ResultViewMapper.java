package net.mewk.ese.mapper.ui;

import javafx.scene.control.TreeItem;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.query.ResultData;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@Singleton
public class ResultViewMapper implements Mapper<SearchResponse, TreeItem<Object>> {

    @Override
    public TreeItem<Object> map(SearchResponse object) {
        TreeItem<Object> resultItem = new TreeItem<>(object);

        for (SearchHit searchHit : object.getHits().getHits()) {
            resultItem.getChildren().add(mapSearchHit(searchHit));
        }

        resultItem.getChildren().sort(Comparator.comparing(Object::toString));

        return resultItem;
    }

    private TreeItem<Object> mapSearchHit(SearchHit searchHit) {
        TreeItem<Object> typeItem = new TreeItem<>(new ResultData(searchHit.index(), searchHit.type(), searchHit.sourceAsMap(), searchHit.score()));

        for (Map.Entry<String, Object> sourceEntry : searchHit.getSource().entrySet()) {
            typeItem.getChildren().add(mapSource(searchHit, sourceEntry));
        }

        typeItem.getChildren().sort(Comparator.comparing(Object::toString));

        return typeItem;
    }

    @SuppressWarnings("unchecked")
    private TreeItem<Object> mapSource(SearchHit searchHit, Map.Entry<String, Object> sourceEntry) {
        TreeItem<Object> sourceItem = new TreeItem<>(new ResultData(searchHit.index(), sourceEntry.getKey(), sourceEntry.getValue(), null));

        if (sourceEntry.getValue() instanceof Map) {
            for (Map.Entry<String, Object> _sourceEntry : ((Map<String, Object>) sourceEntry.getValue()).entrySet()) {
                sourceItem.getChildren().add(mapSource(searchHit, _sourceEntry));
            }
        } else if (sourceEntry.getValue() instanceof List) {
            int i = 0;
            for (Object _sourceEntry : (List<Object>) sourceEntry.getValue()) {
                sourceItem.getChildren().add(mapSource(searchHit, new AbstractMap.SimpleEntry<String, Object>(String.valueOf(i), _sourceEntry)));
                i++;
            }
        }

        sourceItem.getChildren().sort(Comparator.comparing(Object::toString));

        return sourceItem;
    }
}

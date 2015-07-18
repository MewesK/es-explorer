package net.mewk.ese.mapper.ui;

import javafx.scene.control.TreeItem;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.ResultData;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.util.Comparator;
import java.util.Map;

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

    private TreeItem<Object> mapSource(SearchHit searchHit, Map.Entry<String, Object> sourceEntry) {
        TreeItem<Object> sourceItem = new TreeItem<>(new ResultData(searchHit.index(), sourceEntry.getKey(), sourceEntry.getValue(), null));

        if (sourceEntry.getValue() instanceof Map) {
            for (Map.Entry<String, Object> _sourceEntry : ((Map<String, Object>) sourceEntry.getValue()).entrySet()) {
                sourceItem.getChildren().add(mapSource(searchHit, _sourceEntry));
            }
        }

        sourceItem.getChildren().sort(Comparator.comparing(Object::toString));

        return sourceItem;
    }
}

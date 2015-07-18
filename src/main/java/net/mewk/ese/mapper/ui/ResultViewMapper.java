package net.mewk.ese.mapper.ui;

import javafx.scene.control.TreeItem;
import net.mewk.ese.mapper.Mapper;
import org.elasticsearch.action.search.SearchResponse;

public class ResultViewMapper implements Mapper<SearchResponse, TreeItem<Object>> {
    @Override
    public TreeItem<Object> map(SearchResponse object) {
        TreeItem<Object> resultItem = new TreeItem<>(object);

        //for (Map.Entry<String, Type> typeEntry : object.getTypeMap().entrySet()) {
        //indexItem.getChildren().add(mapType(typeEntry.getValue()));
        //}

        //indexItem.getChildren().sort(Comparator.comparing(Object::toString));

        return resultItem;
    }
}

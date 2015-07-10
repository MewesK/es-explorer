package net.mewk.ese.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.elasticsearch.action.search.SearchResponse;

public class Result {

    private ObjectProperty<SearchResponse> searchResponseProperty = new SimpleObjectProperty<>();

    public SearchResponse getSearchResponseProperty() {
        return searchResponseProperty.get();
    }

    public ObjectProperty<SearchResponse> searchResponsePropertyProperty() {
        return searchResponseProperty;
    }

    public void setSearchResponseProperty(SearchResponse searchResponseProperty) {
        this.searchResponseProperty.set(searchResponseProperty);
    }
}

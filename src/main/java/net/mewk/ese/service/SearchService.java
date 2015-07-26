package net.mewk.ese.service;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.mewk.ese.mapper.data.ResultMapper;
import net.mewk.ese.model.connection.Connection;
import net.mewk.ese.model.result.Result;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.inject.Inject;

public class SearchService extends Service<Result> {

    private final ObjectProperty<Connection> connection = new SimpleObjectProperty<>();
    private final StringProperty query = new SimpleStringProperty();
    private final ListProperty<String> indices = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Inject
    private ResultMapper resultMapper;

    @Override
    protected Task<Result> createTask() {
        return new Task<Result>() {
            @Override
            protected Result call() throws Exception {
                // Connect
                Settings settings = ImmutableSettings.settingsBuilder()
                        .put("client.transport.sniff", true)
                        .build();
                InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(
                        connection.get().getHostname(),
                        connection.get().getPort());
                Client client = new TransportClient(settings).addTransportAddress(transportAddress);

                // Get data
                SearchRequest searchRequest = new SearchRequest(
                        indices.get().toArray(new String[indices.size()]),
                        query.get().getBytes());
                ActionFuture<SearchResponse> searchResponseActionFuture = client.search(searchRequest);

                // Map data
                return resultMapper.map(searchResponseActionFuture.get());
            }
        };
    }

    public Connection getConnection() {
        return connection.get();
    }

    public ObjectProperty<Connection> connectionProperty() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection.set(connection);
    }

    public String getQuery() {
        return query.get();
    }

    public StringProperty queryProperty() {
        return query;
    }

    public void setQuery(String query) {
        this.query.set(query);
    }

    public ObservableList<String> getIndices() {
        return indices.get();
    }

    public ListProperty<String> indicesProperty() {
        return indices;
    }

    public void setIndices(ObservableList<String> indices) {
        this.indices.set(indices);
    }
}

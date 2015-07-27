package net.mewk.fx.ese.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.mewk.fx.ese.mapper.data.ResultMapper;
import net.mewk.fx.ese.model.connection.Connection;
import net.mewk.fx.ese.model.query.Query;
import net.mewk.fx.ese.model.result.Result;
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
    private final ObjectProperty<Query> query = new SimpleObjectProperty<>();

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
                        query.get().getIndexNameList().toArray(new String[query.get().getIndexNameList().size()]),
                        query.get().getQuery().getBytes());
                ActionFuture<SearchResponse> searchResponseActionFuture = client.search(searchRequest);

                // Map data
                Result result = resultMapper.map(searchResponseActionFuture.get());
                result.setQuery(getQuery().clone());

                return result;
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

    public Query getQuery() {
        return query.get();
    }

    public ObjectProperty<Query> queryProperty() {
        return query;
    }

    public void setQuery(Query query) {
        this.query.set(query);
    }
}

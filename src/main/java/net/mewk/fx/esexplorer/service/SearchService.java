package net.mewk.fx.esexplorer.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.mewk.fx.esexplorer.mapper.data.ResultMapper;
import net.mewk.fx.esexplorer.model.connection.Connection;
import net.mewk.fx.esexplorer.model.query.Query;
import net.mewk.fx.esexplorer.model.result.Result;
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

    @Inject
    private Connection connection;

    @Inject
    private Query query;

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
                        connection.getHostname(),
                        connection.getPort());
                Client client = new TransportClient(settings).addTransportAddress(transportAddress);

                // Get data
                SearchRequest searchRequest = new SearchRequest(
                        query.getIndexNameList().toArray(new String[query.getIndexNameList().size()]),
                        query.getQuery().getBytes());
                ActionFuture<SearchResponse> searchResponseActionFuture = client.search(searchRequest);

                // Map data
                Result result = resultMapper.map(searchResponseActionFuture.get());
                result.setQuery(getQuery().clone());

                return result;
            }
        };
    }

    public Connection getConnection() {
        return connection;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }
}

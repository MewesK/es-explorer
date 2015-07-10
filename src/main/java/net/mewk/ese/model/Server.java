package net.mewk.ese.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.mewk.ese.Main;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.hppc.ObjectContainer;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.Serializable;

public class Server implements Serializable {

    private Client client = null;

    protected Connection connection;
    protected ObservableMap<String, Index> indexMap = FXCollections.observableHashMap();

    public Server(Connection connection) {
        this.connection = connection;
    }

    public void start() {
        if (client == null) {
            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("client.transport.sniff", true)
                    .build();
            InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(connection.getHostname(), connection.getPort());
            client = new TransportClient(settings).addTransportAddress(transportAddress);
            initialize();
        }
    }

    public void stop() {
        if (client != null) {
            client.close();
        }
    }

    public void refresh() {
        if (client != null) {
            initialize();
        }
    }

    public Result search(String[] indices, String query) {
        Result result = new Result();

        SearchRequest searchRequest = new SearchRequest(indices, query.getBytes());
        client.search(searchRequest, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                result.setSearchResponseProperty(searchResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                // TODO
            }
        });

        return result;
    };

    private void initialize() {
        // Reset
        indexMap.clear();

        // Get index data
        ObjectContainer<IndexMetaData> indexMetaDataObjectContainer = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().indices().values();
        for (ObjectCursor<IndexMetaData> indexMetaDataObjectCursor : indexMetaDataObjectContainer) {
            Index index = (Index) Main.getMapperManager().findByClass(Index.class).map(indexMetaDataObjectCursor.value);
            indexMap.put(index.getName(), index);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ObservableMap<String, Index> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(ObservableMap<String, Index> indexMap) {
        this.indexMap = indexMap;
    }
}

package net.mewk.ese.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.mewk.ese.Main;
import net.mewk.ese.mapper.es.IndexMapper;
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
    private Connection connection;
    private ObservableMap<String, Index> indexMap = FXCollections.observableHashMap();

    public Server(Connection connection) {
        this.connection = connection;
    }

    private void initialize() {
        // Reset
        indexMap.clear();

        // Get index data
        ObjectContainer<IndexMetaData> indexMetaDataObjectContainer = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().indices().values();
        for (ObjectCursor<IndexMetaData> indexMetaDataObjectCursor : indexMetaDataObjectContainer) {
            Index index = (Index) Main.getMapperManager().findByClass(IndexMapper.class).map(indexMetaDataObjectCursor.value);
            indexMap.put(index.getName(), index);
        }
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

    public void search(String[] indices, String query, ActionListener<SearchResponse> actionListener) {
        client.search(new SearchRequest(indices, query.getBytes()), actionListener);
    }

    ;

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

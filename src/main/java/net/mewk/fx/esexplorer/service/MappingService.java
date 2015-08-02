package net.mewk.fx.esexplorer.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.mewk.fx.esexplorer.mapper.data.MappingMapper;
import net.mewk.fx.esexplorer.model.connection.Connection;
import net.mewk.fx.esexplorer.model.mapping.Mapping;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.hppc.ObjectContainer;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.inject.Inject;

public class MappingService extends Service<Mapping> {

    @Inject
    private Connection connection;

    @Inject
    private MappingMapper mappingMapper;

    @Override
    protected Task<Mapping> createTask() {
        return new Task<Mapping>() {
            @Override
            protected Mapping call() throws Exception {
                // Connect
                Settings settings = ImmutableSettings.settingsBuilder()
                        .put("client.transport.sniff", true)
                        .build();
                InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(
                        connection.getHostname(),
                        connection.getPort());
                Client client = new TransportClient(settings).addTransportAddress(transportAddress);

                // Get data
                ObjectContainer<IndexMetaData> indexMetaDataObjectContainer = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().indices().values();

                // Map data
                Mapping mapping = mappingMapper.map(indexMetaDataObjectContainer);
                mapping.setConnection(connection);

                return mapping;
            }
        };
    }

    public Connection getConnection() {
        return connection;
    }
}

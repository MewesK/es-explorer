package net.mewk.ese.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.mewk.ese.mapper.data.MappingMapper;
import net.mewk.ese.model.connection.Connection;
import net.mewk.ese.model.mapping.Mapping;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.hppc.ObjectContainer;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.inject.Inject;

public class MappingService extends Service<Mapping> {

    private ObjectProperty<Connection> connection = new SimpleObjectProperty<>();

    @Inject
    MappingMapper mappingMapper;

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
                        connection.get().getHostname(),
                        connection.get().getPort());
                Client client = new TransportClient(settings).addTransportAddress(transportAddress);

                // Get data
                ObjectContainer<IndexMetaData> indexMetaDataObjectContainer = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().indices().values();

                // Map data
                Mapping mapping = mappingMapper.map(indexMetaDataObjectContainer);
                mapping.setConnection(connection.get());

                return mapping;
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
}

package net.mewk.ese.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.mewk.ese.model.Connection;

public class ConnectionManager {

    private ObservableList<Connection> connectionList = FXCollections.observableArrayList();

    public void stop() {
        // TODO: persist to disk
    }

    public ObservableList<Connection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(ObservableList<Connection> connectionList) {
        this.connectionList = connectionList;
    }
}

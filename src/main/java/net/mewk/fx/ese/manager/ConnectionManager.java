package net.mewk.fx.ese.manager;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.mewk.fx.ese.model.connection.Connection;

import javax.inject.Singleton;

@Singleton
public class ConnectionManager {

    private final ListProperty<Connection> connectionList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Connection> activeConnectionList = new SimpleListProperty<>(FXCollections.observableArrayList());

    // Property access

    public ObservableList<Connection> getConnectionList() {
        return connectionList.get();
    }

    public ListProperty<Connection> connectionListProperty() {
        return connectionList;
    }

    public void setConnectionList(ObservableList<Connection> connectionList) {
        this.connectionList.set(connectionList);
    }

    public ObservableList<Connection> getActiveConnectionList() {
        return activeConnectionList.get();
    }

    public ListProperty<Connection> activeConnectionListProperty() {
        return activeConnectionList;
    }

    public void setActiveConnectionList(ObservableList<Connection> activeConnectionList) {
        this.activeConnectionList.set(activeConnectionList);
    }
}

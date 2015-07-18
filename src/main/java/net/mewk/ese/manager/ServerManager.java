package net.mewk.ese.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.mewk.ese.model.connection.Connection;
import net.mewk.ese.model.server.Server;
import net.mewk.ese.presenter.ServerPresenter;
import net.mewk.ese.view.ServerView;

import java.util.Map;

public class ServerManager {

    private ObservableMap<Connection, ServerView> serverMap = FXCollections.observableHashMap();

    public ServerView create(Connection connection) {
        Server server = new Server(connection);
        server.start();

        ServerView serverView = new ServerView();
        ((ServerPresenter) serverView.getPresenter()).setServer(server);

        serverMap.put(connection, serverView);

        return serverView;
    }

    public void stop() {
        for (Map.Entry<Connection, ServerView> server : serverMap.entrySet()) {
            ((ServerPresenter) server.getValue().getPresenter()).getServer().stop();
        }
    }

    public ObservableMap<Connection, ServerView> getServerMap() {
        return serverMap;
    }

    public void setServerMap(ObservableMap<Connection, ServerView> serverMap) {
        this.serverMap = serverMap;
    }
}

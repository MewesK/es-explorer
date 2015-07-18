package net.mewk.ese.presenter;

import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.mewk.ese.manager.ServerManager;
import net.mewk.ese.model.connection.Connection;
import net.mewk.ese.view.ConnectionView;
import net.mewk.ese.view.ServerView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPresenter implements Initializable {

    @Inject
    private ServerManager serverManager;

    @FXML
    public TabPane mainTabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectionView connectionView = new ConnectionView();

        Tab connectionTab = new Tab();
        connectionTab.setClosable(false);
        connectionTab.setText("Connections");
        connectionTab.setContent(connectionView.getView());

        mainTabPane.getTabs().add(connectionTab);

        serverManager.getServerMap().addListener((MapChangeListener<Connection, ServerView>) change -> {
            if (change.wasAdded()) {
                Tab serverTab = new Tab();
                serverTab.setClosable(true);
                serverTab.setText(change.getKey().getName());
                serverTab.setContent(change.getValueAdded().getView());

                mainTabPane.getTabs().add(serverTab);
                mainTabPane.getSelectionModel().select(serverTab);
            } else {
                // TODO
            }
        });
    }
}

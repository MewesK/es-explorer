package net.mewk.fx.ese.presenter;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.mewk.fx.ese.manager.ConnectionManager;
import net.mewk.fx.ese.model.connection.Connection;
import net.mewk.fx.ese.view.ConnectionView;
import net.mewk.fx.ese.view.ServerView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPresenter implements Initializable {

    private static final Logger LOG = LogManager.getLogger();

    // Injected objects

    @Inject
    private ConnectionManager connectionManager;

    // View objects

    @FXML
    public TabPane mainTabPane;

    // Initializable

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectionView connectionView = new ConnectionView();

        Tab connectionTab = new Tab();
        connectionTab.setClosable(false);
        connectionTab.setText("Connections");
        connectionTab.setContent(connectionView.getView());

        mainTabPane.getTabs().add(connectionTab);

        connectionManager.getActiveConnectionList().addListener((ListChangeListener<Connection>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Connection connection : change.getAddedSubList()) {
                        ServerView serverView = new ServerView();
                        ((ServerPresenter) serverView.getPresenter()).setConnection(connection);

                        Tab serverTab = new Tab();
                        serverTab.setClosable(true);
                        serverTab.setText(connection.getName());
                        serverTab.setContent(serverView.getView());

                        mainTabPane.getTabs().add(serverTab);
                        mainTabPane.getSelectionModel().select(serverTab);
                    }
                } else {
                    for (Connection connection : change.getRemoved()) {
                        for (Tab tab : mainTabPane.getTabs()) {
                            if (tab.getText().equals(connection.getName())) {
                                mainTabPane.getTabs().remove(tab);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
}

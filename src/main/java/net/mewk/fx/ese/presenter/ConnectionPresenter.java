package net.mewk.fx.ese.presenter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import net.mewk.fx.ese.manager.ConnectionManager;
import net.mewk.fx.ese.model.connection.Connection;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionPresenter implements Initializable {

    // Injected objects

    @Inject
    ConnectionManager connectionManager;

    // View objects

    @FXML
    public TextField nameTextField;
    @FXML
    public TextField hostnameTextField;
    @FXML
    public TextField portTextField;

    // Initializable

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // Event handlers

    @FXML
    public void handleConnectButtonAction(ActionEvent actionEvent) {
        Connection connection = new Connection(
                nameTextField.getText(),
                hostnameTextField.getText(),
                Integer.parseInt(portTextField.getText())
        );

        connectionManager.getConnectionList().add(connection);
        connectionManager.getActiveConnectionList().add(connection);
    }
}

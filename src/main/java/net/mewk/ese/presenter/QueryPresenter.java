package net.mewk.ese.presenter;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import net.mewk.ese.CodeArea;
import net.mewk.ese.Highlighter;
import net.mewk.ese.Main;
import net.mewk.ese.mapper.ui.ResultViewMapper;
import net.mewk.ese.model.server.Server;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;

import java.net.URL;
import java.util.ResourceBundle;

public class QueryPresenter implements Initializable {

    private final ObjectProperty<Server> server = new SimpleObjectProperty<>();

    @FXML
    public CodeArea queryCodeArea;
    @FXML
    public CodeArea resultCodeArea;
    @FXML
    public TreeTableView resultTreeTableView;
    @FXML
    public TreeTableColumn resultTreeTableViewIndexColumn;
    @FXML
    public TreeTableColumn resultTreeTableViewNameColumn;
    @FXML
    public TreeTableColumn resultTreeTableViewValueColumn;
    @FXML
    public TreeTableColumn resultTreeTableViewScoreColumn;

    public void initialize(URL location, ResourceBundle resources) {
        resultTreeTableViewIndexColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("index"));
        resultTreeTableViewNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        resultTreeTableViewValueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        resultTreeTableViewScoreColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("score"));
    }

    public void handleRunButtonAction(ActionEvent actionEvent) {
        if (server.get() != null) {
            // Run search on different thread
            server.get().search(new String[]{"_all"}, queryCodeArea.getText(), new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    TreeItem<Object> resultItem = (TreeItem<Object>) Main.getMapperManager().findByClass(ResultViewMapper.class).map(searchResponse);
                    // Return to FX thread
                    Platform.runLater(() -> {
                        resultTreeTableView.setRoot(resultItem);
                        resultCodeArea.replaceText(searchResponse.toString());
                        resultCodeArea.setStyleSpans(0, Highlighter.computeHighlighting(resultCodeArea.getText()));
                    });
                }

                @Override
                public void onFailure(Throwable e) {
                    // TODO
                }
            });
        }
    }

    public void handleResultTabSelectionChanged(Event event) {
        // Redraw hack
        resultCodeArea.replaceText(resultCodeArea.getText());
        resultCodeArea.setStyleSpans(0, Highlighter.computeHighlighting(resultCodeArea.getText()));
    }

    public Server getServer() {
        return server.get();
    }

    public ObjectProperty<Server> serverProperty() {
        return server;
    }

    public void setServer(Server server) {
        this.server.set(server);
    }
}

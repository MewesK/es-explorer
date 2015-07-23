package net.mewk.ese.presenter;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import net.mewk.ese.CodeArea;
import net.mewk.ese.mapper.ui.ResultViewMapper;
import net.mewk.ese.model.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class QueryPresenter implements Initializable {

    private static final Logger LOG = LogManager.getLogger();

    private final ObjectProperty<Server> server = new SimpleObjectProperty<>();

    @Inject
    ResultViewMapper resultViewMapper;

    @FXML
    public SplitPane querySplitPane;
    @FXML
    public CodeArea queryCodeArea;
    @FXML
    public AnchorPane resultPane;
    @FXML
    public Glyph hideResultPaneButtonGlyph;
    @FXML
    public CodeArea resultCodeArea;
    @FXML
    public TreeTableView<Object> resultTreeTableView;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewIndexColumn;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewNameColumn;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewValueColumn;
    @FXML
    public TreeTableColumn<TableView, Object> resultTreeTableViewScoreColumn;

    public void initialize(URL location, ResourceBundle resources) {
        resultTreeTableViewIndexColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("index"));
        resultTreeTableViewNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        resultTreeTableViewValueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        resultTreeTableViewScoreColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("score"));

        resultPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.intValue() == resultPane.getMinHeight()) {
                    hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
                    if (querySplitPane.getUserData() == null) {
                        querySplitPane.setUserData(0.7);
                    }
                } else {
                    hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
                    querySplitPane.setUserData(null);
                }
            }
        });
    }

    public void handleQueryRunButtonAction(ActionEvent actionEvent) {
        if (server.get() != null) {
            // Run search on different thread
            server.get().search(new String[]{"_all"}, queryCodeArea.getText(), new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    TreeItem<Object> resultItem = resultViewMapper.map(searchResponse);
                    // Return to FX thread
                    Platform.runLater(() -> {
                        resultTreeTableView.setRoot(resultItem);
                        resultCodeArea.replaceText(searchResponse.toString());
                    });
                }

                @Override
                public void onFailure(Throwable e) {
                    LOG.error(e.getMessage(), e);
                }
            });
        }
    }

    public void handleResultTabSelectionChanged(Event event) {
        // Redraw hack
        resultCodeArea.replaceText(resultCodeArea.getText());
        resultCodeArea.applyHightlighting();
    }

    public void handleHideResultPaneButton(ActionEvent actionEvent) {
        if (querySplitPane.getUserData() == null) {
            querySplitPane.setUserData(querySplitPane.getDividerPositions()[0]);
            querySplitPane.setDividerPosition(0, 1);
            hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
        } else {
            querySplitPane.setDividerPosition(0, (double) querySplitPane.getUserData());
            querySplitPane.setUserData(null);
            hideResultPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
        }
    }

    public void handleReformatButtonAction(ActionEvent actionEvent) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();

        try {
            JsonElement je = jp.parse(queryCodeArea.getText());
            String prettyText = gson.toJson(je);
            if (prettyText != null) {
                queryCodeArea.replaceText(prettyText);
            }
        } catch (JsonSyntaxException e) {
            LOG.error(e.getMessage(), e);
        }
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

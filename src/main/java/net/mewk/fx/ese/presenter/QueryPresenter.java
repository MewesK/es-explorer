package net.mewk.fx.ese.presenter;

import com.google.common.io.Files;
import com.google.gson.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import net.mewk.fx.control.codearea.CodeArea;
import net.mewk.fx.ese.mapper.ui.ResultViewMapper;
import net.mewk.fx.ese.model.mapping.Index;
import net.mewk.fx.ese.model.query.Query;
import net.mewk.fx.ese.model.result.Result;
import net.mewk.fx.ese.service.SearchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QueryPresenter implements Initializable {

    private static final Logger LOG = LogManager.getLogger();

    // Properties

    private final ObjectProperty<ServerPresenter> serverPresenter = new SimpleObjectProperty<>();
    private final ObjectProperty<Query> query = new SimpleObjectProperty<>(new Query(null, ""));
    private final ObjectProperty<Result> result = new SimpleObjectProperty<>();

    // Injected objects

    @Inject
    private SearchService searchService;
    @Inject
    private ResultViewMapper resultViewMapper;

    // View objects

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

    // Initializable

    public void initialize(URL location, ResourceBundle resources) {
        // Initialize connection
        serverPresenter.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                searchService.setConnection(newValue.getConnection());
            }
        });

        // Initialize result
        result.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                resultTreeTableView.setRoot(resultViewMapper.map(newValue));
                resultCodeArea.replaceText(newValue.getRaw());
            }
        });

        // Initialize mappingService
        searchService.setOnSucceeded(event -> result.set((Result) event.getSource().getValue()));

        // Initialize queryCodeArea
        queryCodeArea.textProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (newValue1 != null) {
                query.get().setQuery(newValue1);
            }
        });

        // Initialize resultTreeTableView
        resultTreeTableViewIndexColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("index"));
        resultTreeTableViewNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        resultTreeTableViewValueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        resultTreeTableViewScoreColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("score"));

        // Initialize resultPane
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

    // Event handlers

    public void handleQueryRunButtonAction(ActionEvent actionEvent) {
        // Set currently selected indices
        query.get().setIndexNameList(
                serverPresenter.get().getCheckedIndices().stream().map(Index::getName).collect(Collectors.toList())
        );

        // TODO: Check for zero indices

        // Start search
        searchService.setQuery(query.get());
        searchService.restart();
    }


    public void handleReformatButtonAction(ActionEvent actionEvent) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();

        try {
            JsonElement je = jp.parse(queryCodeArea.getText());
            if (!(je instanceof JsonNull)) {
                queryCodeArea.replaceText(gson.toJson(je));
            }
        } catch (JsonSyntaxException e) {
            LOG.info(e.getMessage());
        }
    }

    public void handleRefreshResultAction(ActionEvent actionEvent) {
        searchService.setQuery(result.get().getQuery());
        searchService.restart();
    }

    public void handleSaveResultAction(ActionEvent actionEvent) {
        if (result.get() != null) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", ".json"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    Files.write(result.get().getRaw().getBytes(), file);
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public void handleCopyResultAction(ActionEvent actionEvent) {
        if (result.get() != null) {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(result.get().getRaw());
            clipboard.setContent(content);
        }
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

    // Property access

    public ServerPresenter getServerPresenter() {
        return serverPresenter.get();
    }

    public ObjectProperty<ServerPresenter> serverPresenterProperty() {
        return serverPresenter;
    }

    public void setServerPresenter(ServerPresenter serverPresenter) {
        this.serverPresenter.set(serverPresenter);
    }

    public Query getQuery() {
        return query.get();
    }

    public ObjectProperty<Query> queryProperty() {
        return query;
    }

    public void setQuery(Query query) {
        this.query.set(query);
    }

    public Result getResult() {
        return result.get();
    }

    public ObjectProperty<Result> resultProperty() {
        return result;
    }

    public void setResult(Result result) {
        this.result.set(result);
    }
}

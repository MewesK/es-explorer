package net.mewk.fx.esexplorer.presenter;

import com.google.common.io.Files;
import com.google.gson.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import net.mewk.fx.control.codearea.CodeArea;
import net.mewk.fx.control.codearea.style.provider.ActiveProvider;
import net.mewk.fx.control.codearea.style.provider.elasticsearch.ElasticsearchKeywordProvider;
import net.mewk.fx.control.codearea.style.provider.json.JsonErrorProvider;
import net.mewk.fx.control.codearea.style.provider.json.JsonSyntaxProvider;
import net.mewk.fx.esexplorer.mapper.ui.ResultViewMapper;
import net.mewk.fx.esexplorer.model.mapping.Index;
import net.mewk.fx.esexplorer.model.query.Query;
import net.mewk.fx.esexplorer.model.result.Result;
import net.mewk.fx.esexplorer.service.SearchService;
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

    private final ObjectProperty<Result> result = new SimpleObjectProperty<>();
    private final BooleanProperty loaded = new SimpleBooleanProperty(true);

    // Injected objects

    @Inject
    private Query query;
    @Inject
    private ResultViewMapper resultViewMapper;
    @Inject
    private ServerPresenter serverPresenter;
    @Inject
    private SearchService searchService;

    // View objects

    @FXML
    private SplitPane querySplitPane;
    @FXML
    private CodeArea queryCodeArea;
    @FXML
    private AnchorPane resultPane;
    @FXML
    private Glyph hideResultPaneButtonGlyph;
    @FXML
    private StackPane resultStackPane;
    @FXML
    private ProgressIndicator resultProgressIndicator;
    @FXML
    private CodeArea responseCodeArea;
    @FXML
    private TreeTableView<Object> hitsTreeTableView;
    @FXML
    private TreeTableColumn<TableView, Object> resultTreeTableViewIndexColumn;
    @FXML
    private TreeTableColumn<TableView, Object> resultTreeTableViewNameColumn;
    @FXML
    private TreeTableColumn<TableView, Object> resultTreeTableViewValueColumn;
    @FXML
    private TreeTableColumn<TableView, Object> resultTreeTableViewScoreColumn;

    // Initializable

    public void initialize(URL location, ResourceBundle resources) {

        // Initialize queryCodeArea
        query.setQuery(queryCodeArea.getText());

        // Initialize queryCodeArea
        queryCodeArea.getStyleRangeProviderList().addAll(new JsonErrorProvider(),
                new JsonSyntaxProvider(),
                new ElasticsearchKeywordProvider(),
                new ActiveProvider(queryCodeArea));
        queryCodeArea.textProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (newValue1 != null) {
                query.setQuery(newValue1);
            }
        });

        // Initialize responseCodeArea
        responseCodeArea.getStyleRangeProviderList().addAll(new JsonSyntaxProvider(),
                new ActiveProvider(responseCodeArea));

        // Initialize result
        result.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hitsTreeTableView.setRoot(resultViewMapper.map(newValue));
                responseCodeArea.replaceText(newValue.getRaw());
                loaded.set(true);
            }
        });

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

        // Initialize resultProgressIndicator
        resultProgressIndicator.visibleProperty().bind(loadedProperty().not());

        // Initialize resultStackPane
        resultStackPane.disableProperty().bind(loaded.not());

        // Initialize hitsTreeTableView
        resultTreeTableViewIndexColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("index"));
        resultTreeTableViewNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        resultTreeTableViewValueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        resultTreeTableViewScoreColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("score"));

        // Initialize searchService
        searchService.setOnSucceeded(event -> result.set((Result) event.getSource().getValue()));
    }

    // Event handlers

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

    public void handleQueryRunButtonAction(ActionEvent actionEvent) {
        loaded.set(false);

        // Set currently selected indices
        query.setIndexNameList(
                serverPresenter.getCheckedIndices().stream().map(Index::getName).collect(Collectors.toList())
        );

        // Start search
        searchService.setQuery(query);
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
        loaded.set(false);
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

    // Property access

    public ServerPresenter getServerPresenter() {
        return serverPresenter;
    }

    public Query getQuery() {
        return query;
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

    public boolean getLoaded() {
        return loaded.get();
    }

    public BooleanProperty loadedProperty() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded.set(loaded);
    }
}

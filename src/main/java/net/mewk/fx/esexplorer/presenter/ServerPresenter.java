package net.mewk.fx.esexplorer.presenter;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import net.mewk.fx.esexplorer.mapper.ui.IndexViewMapper;
import net.mewk.fx.esexplorer.model.connection.Connection;
import net.mewk.fx.esexplorer.model.mapping.Index;
import net.mewk.fx.esexplorer.model.mapping.Mapping;
import net.mewk.fx.esexplorer.model.mapping.MetaData;
import net.mewk.fx.esexplorer.model.mapping.MetaDataContainer;
import net.mewk.fx.esexplorer.model.query.Query;
import net.mewk.fx.esexplorer.service.MappingService;
import net.mewk.fx.esexplorer.view.QueryView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ServerPresenter implements Initializable {

    private static final Logger LOG = LogManager.getLogger();

    // Properties

    private final ObjectProperty<Mapping> mapping = new SimpleObjectProperty<>();
    private final BooleanProperty loaded = new SimpleBooleanProperty(false);

    // Injected objects

    @Inject
    private Connection connection;
    @Inject
    private IndexViewMapper indexViewMapper;
    @Inject
    private MappingService mappingService;

    // View objects

    @FXML
    private SplitPane mappingSplitPane;
    @FXML
    private StackPane mappingStackPane;
    @FXML
    private CheckTreeView<Object> mappingTreeView;
    @FXML
    private ProgressIndicator mappingProgressIndicator;
    @FXML
    private AnchorPane propertyPane;
    @FXML
    private Glyph hidePropertyPaneButtonGlyph;
    @FXML
    private TableView<MetaData> propertyTableView;
    @FXML
    private TableColumn<MetaData, String> propertyTableViewNameColumn;
    @FXML
    private TableColumn<MetaData, Object> propertyTableViewValueColumn;
    @FXML
    private TabPane queryTabPane;

    // Initializable

    public void initialize(URL location, ResourceBundle resources) {

        // Initialize mapping

        mapping.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Create root
                CheckBoxTreeItem<Object> rootItem = new CheckBoxTreeItem<>("_all");
                rootItem.setExpanded(true);
                rootItem.setSelected(true);
                mappingTreeView.setRoot(rootItem);

                // Add children
                for (Map.Entry<String, Index> indexEntry : newValue.getIndices().entrySet()) {
                    mappingTreeView.getRoot().getChildren().add(indexViewMapper.map(indexEntry.getValue()));
                    mappingTreeView.getRoot().getChildren().sort(Comparator.comparing(Object::toString));
                }

                loaded.set(true);
            }
        });

        // Initialize mappingProgressIndicator

        mappingProgressIndicator.visibleProperty().bind(loaded.not());

        // Initialize mappingService

        mappingService.setOnSucceeded(event -> mapping.set((Mapping) event.getSource().getValue()));
        mappingService.restart();

        // Initialize mappingStackPane

        mappingStackPane.disableProperty().bind(loaded.not());

        // Initialize mappingTreeView

        mappingTreeView.setCellFactory(param -> new CheckBoxTreeCell<Object>() {
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    try {
                        // Get access to check box (reflection)
                        Field checkBoxField = CheckBoxTreeCell.class.getDeclaredField("checkBox");
                        checkBoxField.setAccessible(true);

                        // Enable/Disable check box based of item class
                        CheckBox checkBox = (CheckBox) checkBoxField.get(this);
                        checkBox.setDisable(!(item instanceof Index));
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        });

        mappingTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Change property table content
            if (newValue != oldValue) {
                // Clear children
                propertyTableView.getItems().clear();

                // Add children
                if (newValue != null && newValue.getValue() instanceof MetaDataContainer) {
                    propertyTableView.getItems().addAll(((MetaDataContainer) newValue.getValue()).getMetaDataList());
                    propertyTableView.getItems().sort(Comparator.comparing(MetaData::getName));
                }
            }
        });

        // Initialize propertyPane

        propertyPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.intValue() == propertyPane.getMinHeight()) {
                    hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
                    if (mappingSplitPane.getUserData() == null) {
                        mappingSplitPane.setUserData(0.7);
                    }
                } else {
                    hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
                    mappingSplitPane.setUserData(null);
                }
            }
        });

        // Initialize propertyTableView

        propertyTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        propertyTableViewValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Create initial query

        try {
            createQuery(null);
        } catch (IOException ignored) {
        }
    }

    // Event handlers

    public void handleCopyMappingAction(ActionEvent actionEvent) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(mapping.get().getRaw());
        clipboard.setContent(content);
    }

    public void handleHidePropertyPaneAction(ActionEvent actionEvent) {
        if (mappingSplitPane.getUserData() == null) {
            mappingSplitPane.setUserData(mappingSplitPane.getDividerPositions()[0]);
            mappingSplitPane.setDividerPosition(0, 1);
            hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
        } else {
            mappingSplitPane.setDividerPosition(0, (double) mappingSplitPane.getUserData());
            mappingSplitPane.setUserData(null);
            hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
        }
    }

    public void handleQueryNewButtonAction(ActionEvent actionEvent) {
        try {
            createQuery(null);
        } catch (IOException ignored) {
        }
    }

    public void handleRefreshMappingAction(ActionEvent actionEvent) {
        loaded.set(false);
        mappingService.restart();
    }

    public void handleSaveMappingAction(ActionEvent actionEvent) {
        if (mapping.get() != null) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", ".json"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    Files.write(mapping.get().getRaw().getBytes(), file);
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    // Methods

    public void createQuery(File file) throws IOException {
        Query query;
        if (file == null || !file.exists()) {
            query = new Query(null, "");
        } else {
            query = new Query(file, Files.toString(file, Charset.defaultCharset()));
        }

        QueryView queryView = new QueryView(connection, query, this);

        Tab queryTab = new Tab();
        queryTab.setClosable(true);
        queryTab.setText(file != null ? file.getName() : "Unsaved Query");
        queryTab.setContent(queryView.getView());
        queryTab.setUserData(queryView);

        queryTabPane.getTabs().add(queryTab);
        queryTabPane.getSelectionModel().select(queryTab);
    }

    public List<Index> getCheckedIndices() {
        List<Index> indexList = Lists.newArrayList();

        // First level under root is the only place for indices
        for (TreeItem<Object> treeItem : mappingTreeView.getRoot().getChildren()) {
            int row = mappingTreeView.getRow(treeItem);
            if (row >= 0 && mappingTreeView.getItemBooleanProperty(row).get()) {
                indexList.add((Index) treeItem.getValue());
            }
        }

        return indexList;
    }

    // Property access

    public Connection getConnection() {
        return connection;
    }

    public Mapping getMapping() {
        return mapping.get();
    }

    public ObjectProperty<Mapping> mappingProperty() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping.set(mapping);
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

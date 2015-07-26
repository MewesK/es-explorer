package net.mewk.ese.presenter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import net.mewk.ese.mapper.ui.IndexViewMapper;
import net.mewk.ese.model.connection.Connection;
import net.mewk.ese.model.mapping.*;
import net.mewk.ese.service.MappingService;
import net.mewk.ese.view.QueryView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;

public class ServerPresenter implements Initializable {

    private static final Logger LOG = LogManager.getLogger();

    // Properties

    private final ObjectProperty<Connection> connection = new SimpleObjectProperty<>();
    private final ObjectProperty<Mapping> mapping = new SimpleObjectProperty<>();

    // Injected objects

    @Inject
    private MappingService mappingService;
    @Inject
    private IndexViewMapper indexViewMapper;

    // View objects

    @FXML
    public SplitPane mappingSplitPane;
    @FXML
    private CheckTreeView<Object> indexTreeView;
    @FXML
    public AnchorPane propertyPane;
    @FXML
    public Glyph hidePropertyPaneButtonGlyph;
    @FXML
    public TableView<MetaData> propertyTableView;
    @FXML
    public TableColumn<MetaData, String> propertyTableViewNameColumn;
    @FXML
    public TableColumn<MetaData, Object> propertyTableViewValueColumn;
    @FXML
    private TabPane queryTabPane;

    // Initializable

    public void initialize(URL location, ResourceBundle resources) {
        // Initialize mappingService
        mappingService.setOnSucceeded(event -> mapping.set((Mapping) event.getSource().getValue()));

        // Initialize propertyTableView
        propertyTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        propertyTableViewValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Initialize connection
        connection.addListener((observable, oldValue, newValue) -> {
            mappingService.setConnection(newValue);

            // Initialize queryTabPane
            QueryView queryView = new QueryView();
            ((QueryPresenter) queryView.getPresenter()).setConnection(newValue);

            Tab queryTab = new Tab();
            queryTab.setClosable(true);
            queryTab.setText("unnamed.json");
            queryTab.setContent(queryView.getView());

            queryTabPane.getTabs().add(queryTab);
            queryTabPane.getSelectionModel().select(queryTab);
        });

        // Initialize mapping
        mapping.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Create root
                CheckBoxTreeItem<Object> rootItem = new CheckBoxTreeItem<>("_all");
                rootItem.setExpanded(true);
                rootItem.setSelected(true);
                indexTreeView.setRoot(rootItem);

                // Add children
                for (Map.Entry<String, Index> indexEntry : newValue.getIndices().entrySet()) {
                    indexTreeView.getRoot().getChildren().add(indexViewMapper.map(indexEntry.getValue()));
                    indexTreeView.getRoot().getChildren().sort(Comparator.comparing(Object::toString));
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

        // Initialize indexTreeView
        indexTreeView.setCellFactory(param -> new CheckBoxTreeCell<Object>() {
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
                        checkBox.setDisable(item instanceof SimpleField || item instanceof SimpleType);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        });

        // TODO: Move into mapper
        indexTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Change property table content
            if (newValue != oldValue) {
                // Clear children
                propertyTableView.getItems().clear();

                // Add children
                if (newValue != null && newValue.getValue() instanceof MetaDataContainer) {
                    MetaDataContainer metaDataContainer = (MetaDataContainer) newValue.getValue();
                    for (MetaData metaData : metaDataContainer.getMetaDataList()) {
                        propertyTableView.getItems().add(metaData);
                    }

                    propertyTableView.getItems().sort(Comparator.comparing(MetaData::getName));
                }
            }
        });

        mappingService.start();
    }

    // Event handlers

    public void handleRefreshMappingAction(ActionEvent actionEvent) {
        mappingService.restart();
    }

    public void handleSaveMappingAction(ActionEvent actionEvent) {

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

    // Property access

    public Connection getConnection() {
        return connection.get();
    }

    public ObjectProperty<Connection> connectionProperty() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection.set(connection);
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
}

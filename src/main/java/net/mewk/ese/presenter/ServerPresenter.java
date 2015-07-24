package net.mewk.ese.presenter;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import net.mewk.ese.mapper.ui.IndexViewMapper;
import net.mewk.ese.model.server.*;
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

    private final ObjectProperty<Server> server = new SimpleObjectProperty<>();

    // Injected objects

    @Inject
    IndexViewMapper indexViewMapper;

    // View objects

    @FXML
    public SplitPane indexSplitPane;
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
    private TabPane serverTabPane;

    // Initializable

    public void initialize(URL location, ResourceBundle resources) {
        propertyTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        propertyTableViewValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        propertyPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.intValue() == propertyPane.getMinHeight()) {
                    hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
                    if (indexSplitPane.getUserData() == null) {
                        indexSplitPane.setUserData(0.7);
                    }
                } else {
                    hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
                    indexSplitPane.setUserData(null);
                }
            }
        });

        server.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Reset
                if (indexTreeView.getRoot() != null) {
                    indexTreeView.getRoot().getChildren().clear();
                }

                // Populate items (adds root item)
                Platform.runLater(() -> {
                    for (Map.Entry<String, Index> indexEntry : newValue.getIndexMap().entrySet()) {
                        addIndex(indexEntry.getValue());
                    }
                });

                // Add index listener
                newValue.getIndexMap().addListener((MapChangeListener<String, Index>) change -> {
                    Platform.runLater(() -> {
                        if (change.wasAdded()) {
                            addIndex(change.getValueAdded());
                        } else if (change.wasRemoved()) {
                            removeIndex(change.getValueRemoved());
                        }
                    });
                });

                // Create empty query tab
                QueryView queryView = new QueryView();
                ((QueryPresenter) queryView.getPresenter()).setServer(server.get());

                Tab queryTab = new Tab();
                queryTab.setClosable(true);
                queryTab.setText("unnamed.json");
                queryTab.setContent(queryView.getView());

                // Add and select empty query tab
                serverTabPane.getTabs().add(queryTab);
                serverTabPane.getSelectionModel().select(queryTab);
            }
        });

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

        indexTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Change property table content
            if (newValue != oldValue) {
                propertyTableView.getItems().clear();

                if (newValue != null && newValue.getValue() instanceof MetaDataContainer) {
                    MetaDataContainer metaDataContainer = (MetaDataContainer) newValue.getValue();
                    for (MetaData metaData : metaDataContainer.getMetaDataList()) {
                        propertyTableView.getItems().add(metaData);
                    }

                    propertyTableView.getItems().sort(Comparator.comparing(MetaData::getName));
                }
            }
        });
    }

    // Event handlers

    public void handleRefreshMappingAction(ActionEvent actionEvent) {
        server.get().refresh();
        //server.set(server.get());
    }

    public void handleSaveMappingAction(ActionEvent actionEvent) {

    }

    public void handleHidePropertyPaneAction(ActionEvent actionEvent) {
        if (indexSplitPane.getUserData() == null) {
            indexSplitPane.setUserData(indexSplitPane.getDividerPositions()[0]);
            indexSplitPane.setDividerPosition(0, 1);
            hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_UP);
        } else {
            indexSplitPane.setDividerPosition(0, (double) indexSplitPane.getUserData());
            indexSplitPane.setUserData(null);
            hidePropertyPaneButtonGlyph.setIcon(FontAwesome.Glyph.CHEVRON_DOWN);
        }
    }

    // Private methods

    private void addIndex(Index index) {
        if (index == null) {
            return;
        }

        if (indexTreeView.getRoot() == null) {
            CheckBoxTreeItem<Object> rootItem = new CheckBoxTreeItem<>("_all");
            rootItem.setExpanded(true);
            rootItem.setSelected(true);
            indexTreeView.setRoot(rootItem);
        }

        indexTreeView.getRoot().getChildren().add(indexViewMapper.map(index));
        indexTreeView.getRoot().getChildren().sort(Comparator.comparing(Object::toString));
    }

    private void removeIndex(Index index) {
        if (index == null) {
            return;
        }

        if (indexTreeView.getRoot() != null) {
            for (TreeItem<Object> treeItem : indexTreeView.getRoot().getChildren()) {
                if (treeItem.getValue().equals(index)) {
                    indexTreeView.getRoot().getChildren().remove(treeItem);
                }
            }
        }
    }

    // Property access

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

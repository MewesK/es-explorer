package net.mewk.ese.presenter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import net.mewk.ese.model.*;
import net.mewk.ese.view.QueryView;

import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;

public class ServerPresenter implements Initializable {

    private ObjectProperty<Server> server = new SimpleObjectProperty<>();

    @FXML
    private TreeView<Object> indexTreeView;
    @FXML
    public TableView<MetaData> propertyTableView;
    @FXML
    public TableColumn<MetaData, String> propertyTableViewNameColumn;
    @FXML
    public TableColumn<MetaData, Object> propertyTableViewValueColumn;
    @FXML
    private TabPane serverTabPane;

    private void addIndex(Index index) {
        if (indexTreeView.getRoot() == null) {
            TreeItem<Object> rootItem = new TreeItem<>("_all");
            rootItem.setExpanded(true);
            indexTreeView.setRoot(rootItem);
        }

        indexTreeView.getRoot().getChildren().add(mapIndex(index));
        indexTreeView.getRoot().getChildren().sort(Comparator.comparing(Object::toString));
    }

    private TreeItem<Object> mapIndex(Index index) {
        TreeItem<Object> indexItem = new TreeItem<>(index);

        for (Map.Entry<String, Type> typeEntry : index.getTypeMap().entrySet()) {
            indexItem.getChildren().add(mapType(typeEntry.getValue()));
        }

        indexItem.getChildren().sort(Comparator.comparing(Object::toString));

        return indexItem;
    }

    private TreeItem<Object> mapType(Type type) {
        TreeItem<Object> typeItem = new TreeItem<>(type);

        for (Map.Entry<String, Field> fieldEntry : type.getFieldMap().entrySet()) {
            typeItem.getChildren().add(mapField(fieldEntry.getValue()));
        }

        typeItem.getChildren().sort(Comparator.comparing(Object::toString));

        return typeItem;
    }

    private TreeItem<Object> mapField(Field field) {
        TreeItem<Object> fieldItem = new TreeItem<>(field);

        if (field instanceof NestedType) {
            for (Map.Entry<String, Field> fieldEntry : ((NestedType) field).getFieldMap().entrySet()) {
                fieldItem.getChildren().add(mapField(fieldEntry.getValue()));
            }

            fieldItem.getChildren().sort(Comparator.comparing(Object::toString));
        }

        return fieldItem;
    }

    private void removeIndex(Index index) {
        if (indexTreeView.getRoot() != null) {
            for (TreeItem<Object> treeItem : (ObservableList<TreeItem<Object>>) indexTreeView.getRoot().getChildren()) {
                if (treeItem.getValue().equals(index)) {
                    indexTreeView.getRoot().getChildren().remove(treeItem);
                }
            }
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        propertyTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        propertyTableViewValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        server.addListener(new ChangeListener<Server>() {
            @Override
            public void changed(ObservableValue<? extends Server> observable, Server oldValue, Server newValue) {
                if (newValue != null) {
                    // Reset
                    if (indexTreeView.getRoot() != null) {
                        indexTreeView.getRoot().getChildren().clear();
                    }

                    // Populate items (adds root item)
                    for (Map.Entry<String, Index> indexEntry : newValue.getIndexMap().entrySet()) {
                        addIndex(indexEntry.getValue());
                    }

                    // Add index listener
                    newValue.getIndexMap().addListener(new MapChangeListener<String, Index>() {
                        @Override
                        public void onChanged(Change<? extends String, ? extends Index> change) {
                            if (change.wasAdded() && change.getValueAdded() != null) {
                                addIndex(change.getValueAdded());
                            } else if (change.wasRemoved() && change.getValueRemoved() != null) {
                                removeIndex(change.getValueRemoved());
                            } else {
                                // TODO
                            }
                        }
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
            }
        });

        ((ReadOnlyObjectProperty<TreeItem<Object>>) indexTreeView.getSelectionModel().selectedItemProperty()).addListener(new ChangeListener<TreeItem<Object>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldValue, TreeItem<Object> newValue) {
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
            }
        });
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

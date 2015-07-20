package net.mewk.ese.mapper.ui;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.server.Field;
import net.mewk.ese.model.server.Index;
import net.mewk.ese.model.server.NestedType;
import net.mewk.ese.model.server.Type;

import javax.inject.Singleton;
import java.util.Comparator;
import java.util.Map;

@Singleton
public class IndexViewMapper implements Mapper<Index, TreeItem<Object>> {

    @Override
    public CheckBoxTreeItem<Object> map(Index object) {
        CheckBoxTreeItem<Object> indexItem = new CheckBoxTreeItem<>(object);
        indexItem.setSelected(true);

        for (Map.Entry<String, Type> typeEntry : object.getTypeMap().entrySet()) {
            indexItem.getChildren().add(mapType(typeEntry.getValue()));
        }

        indexItem.getChildren().sort(Comparator.comparing(Object::toString));

        return indexItem;
    }

    private CheckBoxTreeItem<Object> mapType(Type type) {
        CheckBoxTreeItem<Object> typeItem = new CheckBoxTreeItem<>(type);
        typeItem.setSelected(true);

        for (Map.Entry<String, Field> fieldEntry : type.getFieldMap().entrySet()) {
            typeItem.getChildren().add(mapField(fieldEntry.getValue()));
        }

        typeItem.getChildren().sort(Comparator.comparing(Object::toString));

        return typeItem;
    }

    private CheckBoxTreeItem<Object> mapField(Field field) {
        CheckBoxTreeItem<Object> fieldItem = new CheckBoxTreeItem<>(field);
        fieldItem.setSelected(true);

        if (field instanceof NestedType) {
            for (Map.Entry<String, Field> fieldEntry : ((NestedType) field).getFieldMap().entrySet()) {
                fieldItem.getChildren().add(mapField(fieldEntry.getValue()));
            }

            fieldItem.getChildren().sort(Comparator.comparing(Object::toString));
        }

        return fieldItem;
    }
}

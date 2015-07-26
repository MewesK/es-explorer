package net.mewk.ese.mapper.ui;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.mapping.Field;
import net.mewk.ese.model.mapping.Index;
import net.mewk.ese.model.mapping.NestedType;
import net.mewk.ese.model.mapping.Type;

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
            indexItem.getChildren().add(mapFromType(typeEntry.getValue()));
        }

        indexItem.getChildren().sort(Comparator.comparing(Object::toString));

        return indexItem;
    }

    private CheckBoxTreeItem<Object> mapFromType(Type type) {
        CheckBoxTreeItem<Object> typeItem = new CheckBoxTreeItem<>(type);
        typeItem.setSelected(true);

        for (Map.Entry<String, Field> fieldEntry : type.getFieldMap().entrySet()) {
            typeItem.getChildren().add(mapFromField(fieldEntry.getValue()));
        }

        typeItem.getChildren().sort(Comparator.comparing(Object::toString));

        return typeItem;
    }

    private CheckBoxTreeItem<Object> mapFromField(Field field) {
        CheckBoxTreeItem<Object> fieldItem = new CheckBoxTreeItem<>(field);
        fieldItem.setSelected(true);

        if (field instanceof NestedType) {
            for (Map.Entry<String, Field> fieldEntry : ((NestedType) field).getFieldMap().entrySet()) {
                fieldItem.getChildren().add(mapFromField(fieldEntry.getValue()));
            }

            fieldItem.getChildren().sort(Comparator.comparing(Object::toString));
        }

        return fieldItem;
    }
}

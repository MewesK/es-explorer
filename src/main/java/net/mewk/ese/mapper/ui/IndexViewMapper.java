package net.mewk.ese.mapper.ui;

import javafx.scene.control.TreeItem;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.server.Field;
import net.mewk.ese.model.server.Index;
import net.mewk.ese.model.server.NestedType;
import net.mewk.ese.model.server.Type;

import java.util.Comparator;
import java.util.Map;

public class IndexViewMapper implements Mapper<Index, TreeItem<Object>> {
    @Override
    public TreeItem<Object> map(Index object) {
        TreeItem<Object> indexItem = new TreeItem<>(object);

        for (Map.Entry<String, Type> typeEntry : object.getTypeMap().entrySet()) {
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
}

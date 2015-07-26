package net.mewk.ese.mapper.ui;

import javafx.scene.control.TreeItem;
import net.mewk.ese.mapper.Mapper;
import net.mewk.ese.model.result.Hit;
import net.mewk.ese.model.result.Result;

import javax.inject.Singleton;
import java.util.Comparator;

@Singleton
public class ResultViewMapper implements Mapper<Result, TreeItem<Object>> {

    @Override
    public TreeItem<Object> map(Result object) {
        TreeItem<Object> resultItem = new TreeItem<>(object);

        for (Hit hit : object.getHits()) {
            resultItem.getChildren().add(mapFromHit(hit));
        }

        resultItem.getChildren().sort(Comparator.comparing(Object::toString));

        return resultItem;
    }

    private TreeItem<Object> mapFromHit(Hit hit) {
        TreeItem<Object> typeItem = new TreeItem<>(hit);

        for (Hit innerHit : hit.getHits()) {
            typeItem.getChildren().add(mapFromHit(innerHit));
        }

        typeItem.getChildren().sort(Comparator.comparing(Object::toString));

        return typeItem;
    }
}

package net.mewk.fx.esexplorer.model.mapping;

import java.util.Map;

public interface Type extends MetaDataContainer {

    String getName();

    Map<String, Field> getFieldMap();
}

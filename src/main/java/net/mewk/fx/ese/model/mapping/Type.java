package net.mewk.fx.ese.model.mapping;

import java.util.Map;

public interface Type extends MetaDataContainer {

    String getName();

    Map<String, Field> getFieldMap();
}

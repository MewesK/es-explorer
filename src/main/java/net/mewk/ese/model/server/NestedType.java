package net.mewk.ese.model.server;

public class NestedType extends SimpleType implements Field {

    public NestedType(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return null;
    }
}

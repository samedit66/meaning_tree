package org.vstu.meaningtree.nodes.types.containers;

import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.interfaces.Generic;

import java.util.Objects;

public class PlainCollectionType extends Type implements Generic {
    private Type _itemType;

    public PlainCollectionType(Type itemType) {
        _itemType = itemType;
    }

    @Override
    public Type[] getTypeParameters() {
        return new Type[] {_itemType};
    }

    public Type getItemType() {
        return _itemType;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainCollectionType that = (PlainCollectionType) o;
        return Objects.equals(_itemType, that._itemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _itemType);
    }

    @Override
    public PlainCollectionType clone() {
        PlainCollectionType obj = (PlainCollectionType) super.clone();
        obj._itemType = _itemType.clone();
        return obj;
    }
}

package org.vstu.meaningtree.nodes.types.containers.components;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Shape extends Node {
    // Количество измерений
    private final int _dimensionCount;
    // Размер каждого измерения
    private List<Expression> _dimensions;

    public Shape(int dimensionCount) {
        _dimensionCount = dimensionCount;
        _dimensions = new ArrayList<>();
        for (int i = 0; i < dimensionCount; i++) {
            _dimensions.add(null);
        };
    }

    public Shape(int dimensionCount, Expression... dimensions) {
        this(dimensionCount, List.of(dimensions));
    }

    public Shape(int dimensionCount, List<Expression> dimensions) {
        if (dimensionCount != dimensions.size()) {
            throw new IllegalArgumentException();
        }

        _dimensionCount = dimensionCount;
        _dimensions = new ArrayList<>(dimensions);
    }

    public int getDimensionCount() {
        return _dimensionCount;
    }

    @Nullable
    public Expression getDimension(int index) {
        try {
            return _dimensions.get(index);
        }
        catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    public List<Expression> getDimensions() {
        return _dimensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape = (Shape) o;
        return _dimensionCount == shape._dimensionCount && Objects.equals(_dimensions, shape._dimensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _dimensionCount, _dimensions);
    }

    @Override
    public Shape clone() {
        Shape obj = (Shape) super.clone();
        obj._dimensions = new ArrayList<>(_dimensions.stream().map(Expression::clone).toList());
        return obj;
    }
}

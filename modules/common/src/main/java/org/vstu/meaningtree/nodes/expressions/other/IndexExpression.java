package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;

import java.util.Objects;

public class IndexExpression extends Expression {
    private Expression _expr;
    // index может содержать ExpressionSequence
    private Expression _index;

    public IndexExpression(Expression expr, Expression index) {
        _expr = expr;
        _index = index;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public Expression getExpr() {
        return _expr;
    }

    public Expression getIndex() {
        return _index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexExpression that = (IndexExpression) o;
        return Objects.equals(_expr, that._expr) && Objects.equals(_index, that._index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _expr, _index);
    }

    @Override
    public IndexExpression clone() {
        IndexExpression obj = (IndexExpression) super.clone();
        obj._expr = _expr.clone();
        obj._index = _index.clone();
        return obj;
    }
}

package org.vstu.meaningtree.nodes.expressions.other;

import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.interfaces.HasInitialization;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;

import java.util.Objects;

public class AssignmentExpression extends Expression implements HasInitialization {
    private Expression _lvalue;
    private Expression _rvalue;
    private final AugmentedAssignmentOperator _op;

    public AssignmentExpression(Expression id, Expression value, AugmentedAssignmentOperator op) {
        _lvalue = id;
        _rvalue = value;
        _op = op;
    }

    public AssignmentStatement toStatement() {
        return new AssignmentStatement(_lvalue, _rvalue, _op);
    }

    public AssignmentExpression(Expression id, Expression value) {
        this(id, value, AugmentedAssignmentOperator.NONE);
    }

    public AugmentedAssignmentOperator getAugmentedOperator() {
        return _op;
    }

    public Expression getLValue() {
        return _lvalue;
    }

    public Expression getRValue() {
        return _rvalue;
    }

    @Override
    public String generateDot() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s [label=\"%s\"];\n", _id, _op.toString()));

        builder.append(_lvalue.generateDot());
        builder.append(_rvalue.generateDot());

        builder.append(String.format("%s -- %s;\n", _id, _lvalue.getId()));
        builder.append(String.format("%s -- %s;\n", _id, _rvalue.getId()));

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentExpression that = (AssignmentExpression) o;
        return Objects.equals(_lvalue, that._lvalue) && Objects.equals(_rvalue, that._rvalue) && _op == that._op;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _lvalue, _rvalue, _op);
    }

    @Override
    public AssignmentExpression clone() {
        AssignmentExpression obj = (AssignmentExpression) super.clone();
        obj._lvalue = _lvalue.clone();
        obj._rvalue = _rvalue.clone();
        return obj;
    }
}

package org.vstu.meaningtree.nodes.logical;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class LongCircuitAndOp extends BinaryExpression {
    public LongCircuitAndOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}

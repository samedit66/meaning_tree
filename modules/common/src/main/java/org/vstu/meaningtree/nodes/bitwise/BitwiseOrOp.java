package org.vstu.meaningtree.nodes.bitwise;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Expression;

public class BitwiseOrOp extends BinaryExpression {

    public BitwiseOrOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}

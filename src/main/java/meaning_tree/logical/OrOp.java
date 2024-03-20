package meaning_tree.logical;

import meaning_tree.BinaryExpression;
import meaning_tree.Expression;

public class OrOp extends BinaryExpression {
    public OrOp(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }
}
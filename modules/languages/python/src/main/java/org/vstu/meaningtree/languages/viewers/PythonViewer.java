package org.vstu.meaningtree.languages.viewers;

import org.vstu.meaningtree.nodes.BinaryExpression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.UnaryExpression;
import org.vstu.meaningtree.nodes.bitwise.*;
import org.vstu.meaningtree.nodes.comparison.BinaryComparison;
import org.vstu.meaningtree.nodes.comparison.CompoundComparison;
import org.vstu.meaningtree.nodes.logical.NotOp;
import org.vstu.meaningtree.nodes.math.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.IfStatement;
import org.vstu.meaningtree.nodes.unary.*;


public class PythonViewer extends Viewer {
    /*
    TODO:
     - comparisons
     - unary/binary ops
     - WHAT about priority?
     - tabulation
     - support if/for/for-each/while/ternary/do-while/switch
     - assignment support, variable declaration
     - parenthesize
     - literal support, comment support
     - break/return/contiune
     - identifiers
     - program entry point
     - general for-loop transformation
     - compound comparison detection
     - support function calls and new object/array, indexing
     - function support
     - class support
     - import support
     */

    @Override
    public String toString(Node node) {
        if (node instanceof BinaryExpression) {
            return binaryOpToString((BinaryExpression) node);
        } else if (node instanceof IfStatement) {
            return conditionToString(node);
        } else if (node instanceof UnaryExpression) {
            return unaryToString((UnaryExpression) node);
        } else if (node instanceof CompoundStatement) {
            return blockToString((CompoundStatement) node);
        } else if (node instanceof BinaryComparison) {
            return comparisonToString(node);
        } else if (node instanceof CompoundComparison) {
            return compoundComparisonToString(node);
        } else {
            return "";
        }
    }

    private String binaryOpToString(BinaryExpression node) {
        String pattern = "";
        if (node instanceof AddOp) {
            pattern = "%s + %s";
        } else if (node instanceof SubOp) {
            pattern = "%s - %s";
        } else if (node instanceof MulOp) {
            pattern = "%s * %s";
        } else if (node instanceof DivOp) {
            pattern = "%s / %s";
        } else if (node instanceof PowOp) {
            pattern = "%s ** %s";
        } else if (node instanceof FloorDivOp) {
            pattern = "%s // %s";
        } else if (node instanceof ModOp) {
            pattern = "%s %% %s";
        } else if (node instanceof BitwiseAndOp) {
            pattern = "%s & %s";
        } else if (node instanceof BitwiseOrOp) {
            pattern = "%s | %s";
        } else if (node instanceof RightShiftOp) {
            pattern = "%s >> %s";
        } else if (node instanceof LeftShiftOp) {
            pattern = "%s << %s";
        } else if (node instanceof XorOp) {
            pattern = "%s ^ %s";
        }
        return String.format(pattern, toString(node.getLeft()), toString(node.getRight()));
    }

    private String conditionToString(Node node) {
        return "";
    }

    private String unaryToString(UnaryExpression node) {
        String pattern = "";
        if (node instanceof UnaryPlusOp) {
            pattern = "+%s";
        } else if (node instanceof UnaryMinusOp) {
            pattern = "-%s";
        } else if (node instanceof NotOp) {
            pattern = "not %s";
        } else if (node instanceof InversionOp) {
            pattern = "~%s";
        } else if (node instanceof PostfixDecrementOp || node instanceof PrefixDecrementOp) {
            pattern = "%s -= 1";
        } else if (node instanceof PostfixIncrementOp || node instanceof PrefixIncrementOp) {
            pattern = "%s += 1";
        }
        return String.format(pattern, toString(node.getArgument()));
    }

    private String blockToString(CompoundStatement node) {
        StringBuilder builder = new StringBuilder();
        for (Node child : node) {
            builder.append("    ");
            builder.append(toString(node));
            builder.append('\n');
        }
        return builder.toString();
    }

    private String comparisonToString(Node node) {
        return "";
    }

    private String compoundComparisonToString(Node node) {
        return "";
    }
}
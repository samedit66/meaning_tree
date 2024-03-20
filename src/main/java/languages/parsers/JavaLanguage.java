package languages.parsers;
import meaning_tree.*;
import meaning_tree.math.*;
import meaning_tree.literals.*;
import meaning_tree.logical.*;
import meaning_tree.comparasion.*;
import org.treesitter.*;

import java.io.File;
import java.io.IOException;

public class JavaLanguage extends Language {
    public MeaningTree getMeaningTree(String code) {
        _code = code;
        TSParser parser = new TSParser();
        TSLanguage javaLanguage = new TreeSitterJava();

        parser.setLanguage(javaLanguage);
        TSTree tree = parser.parseString(null, code);
        try {
            tree.printDotGraphs(new File("TSTree.dot"));
        } catch (IOException _) { }

        return new MeaningTree(fromTSNode(tree.getRootNode()));
    }

    private Node fromTSNode(TSNode node) {
        return switch (node.getType()) {
            case "program" -> fromProgramTSNode(node);
            case "block" -> fromBlockTSNode(node);
            case "statement" -> fromStatementTSNode(node);
            case "if_statement" -> fromIfStatementTSNode(node);
            case "condition" -> fromConditionTSNode(node);
            case "expression_statement" -> fromExpressionStatementTSNode(node);
            case "parenthesized_expression" -> fromParenthesizedExpressionTSNode(node);
            case "binary_expression" -> fromBinaryExpressionTSNode(node);
            case "unary_expression" -> fromUnaryExpressionTSNode(node);
            case "decimal_integer_literal" -> fromIntegerLiteralTSNode(node);
            case "decimal_floating_point_literal" -> fromFloatLiteralTSNode(node);
            case null, default -> throw new UnsupportedOperationException(STR."Can't parse \{node.getType()}");
        };
    }

    private Node fromProgramTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromBlockTSNode(TSNode node) {
        CompoundStatement compoundStatement = new CompoundStatement();
        for (int i = 0; i < node.getChildCount(); i++) {
            Node child = fromTSNode(node.getChild(i));
            compoundStatement.add(child);
        }
        return compoundStatement;
    }

    private Node fromStatementTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromIfStatementTSNode(TSNode node) {
        Expression condition = (Expression) fromTSNode(node.getChildByFieldName("condition"));
        CompoundStatement consequence = (CompoundStatement) fromTSNode(node.getChildByFieldName("consequence"));
        CompoundStatement alternative = (CompoundStatement) fromTSNode(node.getChildByFieldName("alternative"));
        return new IfStatement(condition, consequence, alternative);
    }

    private Node fromConditionTSNode(TSNode node) {
        // Тут что-то сложнее должно быдь...
        return fromTSNode(node.getChildByFieldName("binary_expression"));
    }

    private Node fromExpressionStatementTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromParenthesizedExpressionTSNode(TSNode node) {
        return fromTSNode(node.getChild(0));
    }

    private Node fromIntegerLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new IntegerLiteral(value);
    }

    private Node fromFloatLiteralTSNode(TSNode node) {
        String value = getCodePiece(node);
        return new FloatLiteral(value);
    }

    private Node fromUnaryExpressionTSNode(TSNode node) {
        Expression argument = (Expression) fromTSNode(node.getChildByFieldName("operand"));
        TSNode operation = node.getChildByFieldName("operator");
        return switch (getCodePiece(operation)) {
            case "!" -> new NotOp(argument);
            case null, default -> throw new UnsupportedOperationException();
        };
    }

    private Node fromBinaryExpressionTSNode(TSNode node) {
        Expression left = (Expression) fromTSNode(node.getChildByFieldName("left"));
        Expression right = (Expression) fromTSNode(node.getChildByFieldName("right"));
        TSNode operator = node.getChildByFieldName("operator");

        return switch (getCodePiece(operator)) {
            case "+" -> new AddOp(left, right);
            case "-" -> new SubOp(left, right);
            case "*" -> new MulOp(left, right);
            case "/" -> new DivOp(left, right);
            case "<" -> new LtOp(left, right);
            case ">" -> new GtOp(left, right);
            case "==" -> new EqOp(left, right);
            case "!=" -> new NeOp(left, right);
            case ">=" -> new GeOp(left, right);
            case "<=" -> new LeOp(left, right);
            default -> throw new UnsupportedOperationException();
        };
    }


}

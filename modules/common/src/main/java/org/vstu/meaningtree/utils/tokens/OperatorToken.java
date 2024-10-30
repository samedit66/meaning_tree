package org.vstu.meaningtree.utils.tokens;

import org.vstu.meaningtree.exceptions.MeaningTreeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OperatorToken extends OperandToken {
    public final int precedence;
    public final OperatorAssociativity assoc;
    public final OperatorArity arity;
    public final boolean isStrictOrder;
    public final OperatorTokenPosition tokenPos;

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), precedence, assoc, arity, isStrictOrder, tokenPos);
    }

    public OperatorToken(String value,
                         TokenType type,
                         int precedence,
                         OperatorAssociativity assoc,
                         OperatorArity arity,
                         boolean isStrictOrder,
                         OperatorTokenPosition tokenPos
    ) {
        super(value, type);
        this.precedence = precedence;
        this.assoc = assoc;
        this.arity = arity;
        this.tokenPos = tokenPos;
        this.isStrictOrder = isStrictOrder;
    }

    public OperatorToken(String value,
                         TokenType type,
                         int precedence,
                         OperatorAssociativity assoc,
                         OperatorArity arity,
                         boolean isStrictOrder
    ) {
        this(value, type, precedence, assoc, arity, isStrictOrder,
                arity == OperatorArity.UNARY ? OperatorTokenPosition.PREFIX : OperatorTokenPosition.INFIX );
    }

    public static List<OperatorToken> makeTernary(int precedence, OperatorAssociativity assoc,
                                            boolean isStrictOrder, String ... tokens) {
        if (tokens.length != 2) {
            throw new MeaningTreeException("Malformed ternary operator");
        }
        return new ArrayList<>() {{
            for (int i = 0; i < tokens.length; i++) {
                add(new TernaryOperatorToken(tokens[i], i, precedence, assoc, isStrictOrder));
            }
        }};
    }

    @Override
    public String toString() {
        return String.format("token[\"%s\",%s%s,prec=%s,assoc=%s,arity=%s,strictOrder=%s]",
                value, type, getAssignedValue() == null ? "" : ",tag=".concat(getAssignedValue().toString()), precedence, assoc, arity, isStrictOrder);
    }

    public OperatorToken clone() {
        OperatorToken copy = new OperatorToken(value, type, precedence, assoc, arity, isStrictOrder, tokenPos);
        copy.assignValue(assignedValue);
        copy.setMetadata(operandOf, operandPos);
        return copy;
    }
}

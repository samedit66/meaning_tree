package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.TreeSitterUtils;
import org.vstu.meaningtree.utils.tokens.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LanguageTokenizer {
    protected String code;
    protected LanguageParser parser;
    protected LanguageViewer viewer;

    protected abstract Token recognizeToken(TSNode node);

    /**
     * Токенизирует выражения из кода, переданного в токенайзер
     * @return
     */
    public TokenList tokenize(String code) {
        this.code = code;
        parser.getMeaningTree(code);
        TokenList list = new TokenList();
        //TODO: update grammars для языков, так как ошибочный код плохо поддерживается
        collectTokens(parser.getRootNode(), list);
        return list;
    }

    public abstract TokenList tokenize(Node node);

    /**
     * Токенизирует узлы из дерева MeaningTree, с возможностью выведения привязанных к узлам значений
     * @param mt - общее дерево
     * @return
     */
    public TokenList tokenizeExtended(MeaningTree mt) {
        return tokenize(mt.getRootNode());
    }

    public TokenList tokenizeExtended(String code) {
        return tokenizeExtended(parser.getMeaningTree(code));
    }

    /**
     * Список узлов tree sitter дерева, внутрь которых при обходе заходить не нужно, но нужно их обработать целиком
     * @return
     */
    protected List<String> getStopNodes() {
        return List.of();
    }

    protected abstract List<String> getOperatorNodes(OperatorArity arity);

    protected abstract String getFieldNameByOperandPos(OperandPosition pos, String operatorNode);

    protected abstract OperatorToken getOperator(String tokenValue, TSNode node);
    public abstract OperatorToken getOperatorByTokenName(String tokenName);

    public LanguageTokenizer(LanguageParser parser, LanguageViewer viewer) {
        this.parser = parser;
        this.viewer = viewer;
    }

    protected TokenGroup collectTokens(TSNode node, TokenList tokens) {
        int start = tokens.size();
        boolean skipChildren = false;
        if (node.getChildCount() == 0 || getStopNodes().contains(node.getType())) {
            String value = TreeSitterUtils.getCodePiece(code, node);
            if (value.trim().isEmpty()) {
                return null;
            }
            tokens.add(recognizeToken(node));
        } else if (
                getOperatorNodes(OperatorArity.BINARY).contains(node.getType())
                || getOperatorNodes(OperatorArity.TERNARY).contains(node.getType())
        ) {
            OperatorToken token = null;
            Map<OperandPosition, TokenGroup> operands = new HashMap<>();
            for (int i = 0; i < node.getChildCount(); i++) {
                TokenGroup group = collectTokens(node.getChild(i), tokens);
                if (node.getFieldNameForChild(i) != null) {
                    if (node.getFieldNameForChild(i).equals(getFieldNameByOperandPos(OperandPosition.LEFT, node.getType()))) {
                        operands.put(OperandPosition.LEFT, group);
                    } else if (node.getFieldNameForChild(i).equals(getFieldNameByOperandPos(OperandPosition.RIGHT, node.getType()))) {
                        operands.put(OperandPosition.RIGHT, group);
                    } else if (node.getFieldNameForChild(i).equals(getFieldNameByOperandPos(OperandPosition.CENTER, node.getType()))) {
                        operands.put(OperandPosition.CENTER, group);
                    }
                }
                if (group.length() == 1 && tokens.get(group.start) instanceof OperatorToken op && token == null) {
                    token = op;
                }
            }

            for (OperandPosition pos : operands.keySet()) {
                TokenGroup group = operands.get(pos);
                for (int i = group.start; i < group.stop; i++) {
                    if (!(tokens.get(i) instanceof OperandToken)) {
                        tokens.set(i, new OperandToken(tokens.get(i).value, tokens.get(i).type));
                    }
                    ((OperandToken)tokens.get(i)).setMetadata(token, pos);
                }
            }
            skipChildren = true;
        } else if (getOperatorNodes(OperatorArity.UNARY).contains(node.getType())) {
            TokenGroup group = collectTokens(node, tokens);
            int unaryStart = group.start;
            int unaryStop = group.stop;
            OperatorToken token;
            OperandPosition pos = OperandPosition.RIGHT;
            if (tokens.get(unaryStart) instanceof OperatorToken op) {
                token = op;
                unaryStart++;
            } else {
                token = (OperatorToken) tokens.getLast();
                unaryStop--;
                pos = OperandPosition.LEFT;
            }
            for (int i = unaryStart; i < unaryStop; i++) {
                if (!(tokens.get(i) instanceof OperandToken)) {
                    tokens.set(i, new OperandToken(tokens.get(i).value, tokens.get(i).type));
                }
                ((OperandToken)tokens.get(i)).setMetadata(token, pos);
            }
            skipChildren = true;
        }
        for (int i = 0; i < node.getChildCount() && !skipChildren; i++) {
            collectTokens(node.getChild(i), tokens);
        }
        int stop = tokens.size();
        return new TokenGroup(start, stop, tokens);

    }
}

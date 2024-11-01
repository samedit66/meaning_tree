package org.vstu.meaningtree;

import org.apache.jena.sparql.expr.Expr;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.components.ConditionBranch;
import org.vstu.meaningtree.nodes.statements.loops.DoWhileLoop;
import org.vstu.meaningtree.nodes.statements.loops.WhileLoop;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;

import java.util.ArrayList;

public class AugletsRefactorProblemsGenerator {

    public static MeaningTree generate(
            MeaningTree mt,
            AugletsRefactorProblemsType problemType,
            boolean modifyAll
    ) {
        var rootNode = mt.getRootNode();
        if (!(rootNode instanceof ProgramEntryPoint)) {
            throw new IllegalArgumentException("Cannot work with not an ProgramEntryPoint root node");
        }

        boolean hasModified = false;
        var body = new ArrayList<Node>();
        for (var node : ((ProgramEntryPoint) rootNode).getBody()) {
           if (!modifyAll && hasModified) {
               body.add(node);
           }
           else {
               var modifiedIf = generate(node, problemType);
               if (modifiedIf != null) {
                   body.add(modifiedIf);
                   hasModified = true;
               }
               else {
                   body.add(node);
               }
           }
        }

        return new MeaningTree(new ProgramEntryPoint(new SymbolEnvironment(null), body));
    }

    private static Node generate(Node node, AugletsRefactorProblemsType problemType) {
        try {
            return switch (problemType) {
                case ADD_DANGLING_ELSE -> addDanglingEmptyElse((IfStatement) node);
                case ADD_USELESS_CONDITION_CHECKING_IN_ELSE -> addUselessConditionCheckingInElse((IfStatement) node);
                case WRAP_WHILE_LOOP_AND_REPLACE_IT_WITH_DO_WHILE ->
                        wrapWhileLoopAndReplaceItWithDoWhile((WhileLoop) node);
            };
        }
        catch (ClassCastException castException) {
            return node;
        }
    }

    /**
     * Добавляет пустую ветку else, если ее не было ранее.
     * ifStatement должен удовлетворять условие: не иметь ветки else.
     * @param ifStatement Ветвление
     * @return обновленный ifStatement с пустой веткой else, null, если ifStatement
     * не является подходящим
     */
    public static IfStatement addDanglingEmptyElse(IfStatement ifStatement) {
        if (!ifStatement.hasElseBranch()) {
            var emptyElse = new CompoundStatement(new SymbolEnvironment(null));
            return new IfStatement(ifStatement.getBranches(), emptyElse);
        }
        else {
            Node elseBranch = ifStatement.getElseBranch();
            if (elseBranch instanceof IfStatement innerIfStatement) {
                return addDanglingEmptyElse(innerIfStatement);
            }
        }

        return null;
    }

    public static IfStatement addUselessConditionCheckingInElse(IfStatement ifStatement) {
        if (ifStatement.getBranches().size() != 1 || !ifStatement.hasElseBranch()) {
            return null;
        }

        var thenBranch = ifStatement.getBranches().getFirst();
        var elseBranch = ifStatement.getElseBranch();
        var negativeCondition = makeNegativeCondition(thenBranch.getCondition());
        var newElseBranch = new CompoundStatement(
                new SymbolEnvironment(null),
                new IfStatement(negativeCondition, elseBranch)
        );

        return new IfStatement(thenBranch.getCondition(), thenBranch.getBody(), newElseBranch);
    }

    private static Expression makeNegativeCondition(Expression condition) {
        if (condition instanceof LtOp ltOp) {
            return new GeOp(ltOp.getLeft(), ltOp.getRight());
        }
        else if (condition instanceof LeOp leOp) {
            return new GtOp(leOp.getLeft(), leOp.getRight());
        }
        else if (condition instanceof GtOp gtOp) {
            return new LeOp(gtOp.getLeft(), gtOp.getRight());
        }
        else if (condition instanceof GeOp geOp) {
            return new LtOp(geOp.getLeft(), geOp.getRight());
        }
        else if (condition instanceof EqOp eqOp) {
            return new NotEqOp(eqOp.getLeft(), eqOp.getRight());
        }
        else if (condition instanceof NotEqOp notEqOp) {
            return new EqOp(notEqOp.getLeft(), notEqOp.getRight());
        }

        return new NotOp(condition);
    }

    public static IfStatement wrapWhileLoopAndReplaceItWithDoWhile(WhileLoop whileLoop) {
        var condition = whileLoop.getCondition();
        var doWhile = new DoWhileLoop(condition, whileLoop.getBody());
        return new IfStatement(
                condition,
                new CompoundStatement(
                        new SymbolEnvironment(null),
                        doWhile
                )
        );
    }
}

package org.vstu.meaningtree.utils;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.*;
import org.vstu.meaningtree.nodes.expressions.*;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.literals.*;
import org.vstu.meaningtree.nodes.expressions.newexpr.*;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.calls.*;
import org.vstu.meaningtree.nodes.expressions.comprehensions.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.statements.loops.*;
import org.vstu.meaningtree.nodes.statements.loops.control.*;
import org.vstu.meaningtree.nodes.statements.assignments.*;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.types.builtin.*;

public interface NodeVisitor<T> {
    // Base nodes
    T visit(Node node);
    T visit(Expression expression);
    T visit(Statement statement);
    T visit(Type type);
    T visit(Definition definition);
    T visit(Declaration declaration);

    // Expressions
    T visit(BinaryExpression binaryExpression);
    T visit(UnaryExpression unaryExpression);
    T visit(Literal literal);
    T visit(Identifier identifier);
    T visit(NewExpression newExpression);
    T visit(ParenthesizedExpression parenthesizedExpression);
    T visit(AssignmentExpression assignmentExpression);
    T visit(TernaryOperator ternaryOperator);
    T visit(SizeofExpression sizeofExpression);
    T visit(IndexExpression indexExpression);
    T visit(FunctionCall functionCall);
    T visit(ExpressionSequence expressionSequence);
    T visit(Comprehension comprehension);
    T visit(CastTypeExpression castTypeExpression);
    T visit(ObjectNewExpression objectNewExpression);
    T visit(DeleteExpression deleteExpression);
    
    // Literals
    T visit(NumericLiteral numericLiteral);
    T visit(NullLiteral nullLiteral);
    
    // Statements
    T visit(CompoundStatement compoundStatement);
    T visit(ExpressionStatement expressionStatement);
    T visit(Loop loop);
    T visit(WhileLoop whileLoop);
    T visit(DoWhileLoop doWhileLoop);
    T visit(ForEachLoop forEachLoop);
    T visit(GeneralForLoop generalForLoop);
    T visit(InfiniteLoop infiniteLoop);
    T visit(RangeForLoop rangeForLoop);
    T visit(BreakStatement breakStatement);
    T visit(ContinueStatement continueStatement);
    T visit(AssignmentStatement assignmentStatement);
    T visit(DeleteStatement deleteStatement);
    
    // Declarations
    T visit(ClassDeclaration classDeclaration);
    T visit(FieldDeclaration fieldDeclaration);
    T visit(MethodDeclaration methodDeclaration);
    T visit(Annotation annotation);
    T visit(VariableDeclaration variableDeclaration);
    
    // Definitions
    T visit(ClassDefinition classDefinition);
    
    // Types
    T visit(NumericType numericType);
    T visit(UserType userType);
    
    // Comparisons
    T visit(BinaryComparison binaryComparison);
} 
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
import org.vstu.meaningtree.nodes.expressions.unary.UnaryMinusOp;
import org.vstu.meaningtree.nodes.expressions.unary.UnaryPlusOp;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.components.CaseBlock;
import org.vstu.meaningtree.nodes.statements.loops.*;
import org.vstu.meaningtree.nodes.statements.loops.control.*;
import org.vstu.meaningtree.nodes.statements.assignments.*;
import org.vstu.meaningtree.nodes.types.*;
import org.vstu.meaningtree.nodes.types.builtin.*;

public interface NodeVisitor<T> {
    // Base nodes
    default T visit(Node node) { throw new NotImplementedException("visit(Node)"); }
    default T visit(Expression expression) { throw new NotImplementedException("visit(Expression)"); }
    default T visit(Statement statement) { throw new NotImplementedException("visit(Statement)"); }
    default T visit(Type type) { throw new NotImplementedException("visit(Type)"); }
    default T visit(Definition definition) { throw new NotImplementedException("visit(Definition)"); }
    default T visit(Declaration declaration) { throw new NotImplementedException("visit(Declaration)"); }

    // Expressions
    default T visit(BinaryExpression binaryExpression) { throw new NotImplementedException("visit(BinaryExpression)"); }
    default T visit(UnaryMinusOp unaryMinusOp) { throw new NotImplementedException("visit(UnaryMinusOp)"); }
    default T visit(UnaryPlusOp unaryPlusOp) { throw new NotImplementedException("visit(UnaryPlusOp)"); }
    default T visit(UnaryExpression unaryExpression) { throw new NotImplementedException("visit(UnaryExpression)"); }
    default T visit(Literal literal) { throw new NotImplementedException("visit(Literal)"); }
    default T visit(Identifier identifier) { throw new NotImplementedException("visit(Identifier)"); }
    default T visit(NewExpression newExpression) { throw new NotImplementedException("visit(NewExpression)"); }
    default T visit(ParenthesizedExpression parenthesizedExpression) { throw new NotImplementedException("visit(ParenthesizedExpression)"); }
    default T visit(AssignmentExpression assignmentExpression) { throw new NotImplementedException("visit(AssignmentExpression)"); }
    default T visit(TernaryOperator ternaryOperator) { throw new NotImplementedException("visit(TernaryOperator)"); }
    default T visit(SizeofExpression sizeofExpression) { throw new NotImplementedException("visit(SizeofExpression)"); }
    default T visit(IndexExpression indexExpression) { throw new NotImplementedException("visit(IndexExpression)"); }
    default T visit(FunctionCall functionCall) { throw new NotImplementedException("visit(FunctionCall)"); }
    default T visit(ExpressionSequence expressionSequence) { throw new NotImplementedException("visit(ExpressionSequence)"); }
    default T visit(Comprehension comprehension) { throw new NotImplementedException("visit(Comprehension)"); }
    default T visit(CastTypeExpression castTypeExpression) { throw new NotImplementedException("visit(CastTypeExpression)"); }
    default T visit(ObjectNewExpression objectNewExpression) { throw new NotImplementedException("visit(ObjectNewExpression)"); }
    default T visit(DeleteExpression deleteExpression) { throw new NotImplementedException("visit(DeleteExpression)"); }
    
    // Literals
    default T visit(NumericLiteral numericLiteral) { throw new NotImplementedException("visit(NumericLiteral)"); }
    default T visit(FloatLiteral floatLiteral) { throw new NotImplementedException("visit(FloatLiteral)"); }
    default T visit(IntegerLiteral integerLiteral) { throw new NotImplementedException("visit(IntegerLiteral)"); }
    default T visit(ListLiteral listLiteral) { throw new NotImplementedException("visit(ListLiteral)"); }
    default T visit(ArrayLiteral arrayLiteral) { throw new NotImplementedException("visit(ArrayLiteral)"); }
    default T visit(BoolLiteral boolLiteral) { throw new NotImplementedException("visit(BoolLiteral)"); }
    default T visit(CharacterLiteral characterLiteral) { throw new NotImplementedException("visit(CharacterLiteral)"); }
    default T visit(DictionaryLiteral dictionaryLiteral) { throw new NotImplementedException("visit(DictionaryLiteral)"); }
    default T visit(InterpolatedStringLiteral interpolatedStringLiteral) { throw new NotImplementedException("visit(InterpolatedStringLiteral)"); }
    default T visit(StringLiteral stringLiteral) { throw new NotImplementedException("visit(StringLiteral)"); }
    default T visit(UnmodifiableListLiteral unmodifiableListLiteral) { throw new NotImplementedException("visit(UnmodifiableListLiteral)"); }
    default T visit(SetLiteral setLiteral) { throw new NotImplementedException("visit(SetLiteral)"); }
    default T visit(PlainCollectionLiteral plainCollectionLiteral) { throw new NotImplementedException("visit(PlainCollectionLiteral)"); }
    default T visit(NullLiteral nullLiteral) { throw new NotImplementedException("visit(NullLiteral)"); }
    
    // Statements
    default T visit(CompoundStatement compoundStatement) { throw new NotImplementedException("visit(CompoundStatement)"); }
    default T visit(ExpressionStatement expressionStatement) { throw new NotImplementedException("visit(ExpressionStatement)"); }
    default T visit(Loop loop) { throw new NotImplementedException("visit(Loop)"); }
    default T visit(WhileLoop whileLoop) { throw new NotImplementedException("visit(WhileLoop)"); }
    default T visit(DoWhileLoop doWhileLoop) { throw new NotImplementedException("visit(DoWhileLoop)"); }
    default T visit(ForEachLoop forEachLoop) { throw new NotImplementedException("visit(ForEachLoop)"); }
    default T visit(GeneralForLoop generalForLoop) { throw new NotImplementedException("visit(GeneralForLoop)"); }
    default T visit(InfiniteLoop infiniteLoop) { throw new NotImplementedException("visit(InfiniteLoop)"); }
    default T visit(RangeForLoop rangeForLoop) { throw new NotImplementedException("visit(RangeForLoop)"); }
    default T visit(BreakStatement breakStatement) { throw new NotImplementedException("visit(BreakStatement)"); }
    default T visit(ContinueStatement continueStatement) { throw new NotImplementedException("visit(ContinueStatement)"); }
    default T visit(AssignmentStatement assignmentStatement) { throw new NotImplementedException("visit(AssignmentStatement)"); }
    default T visit(DeleteStatement deleteStatement) { throw new NotImplementedException("visit(DeleteStatement)"); }
    default T visit(IfStatement ifStatement) { throw new NotImplementedException("visit(IfStatement)"); }
    default T visit(CaseBlock caseBlock) { throw new NotImplementedException("visit(CaseBlock)"); }
    default T visit(MultipleAssignmentStatement multipleAssignmentStatement) { throw new NotImplementedException("visit(MultipleAssignmentStatement)"); }
    
    // Declarations
    default T visit(ClassDeclaration classDeclaration) { throw new NotImplementedException("visit(ClassDeclaration)"); }
    default T visit(FieldDeclaration fieldDeclaration) { throw new NotImplementedException("visit(FieldDeclaration)"); }
    default T visit(MethodDeclaration methodDeclaration) { throw new NotImplementedException("visit(MethodDeclaration)"); }
    default T visit(Annotation annotation) { throw new NotImplementedException("visit(Annotation)"); }
    default T visit(VariableDeclaration variableDeclaration) { throw new NotImplementedException("visit(VariableDeclaration)"); }
    
    // Definitions
    default T visit(ClassDefinition classDefinition) { throw new NotImplementedException("visit(ClassDefinition)"); }
    
    // Types
    default T visit(NumericType numericType) { throw new NotImplementedException("visit(NumericType)"); }
    default T visit(FloatType floatType) { throw new NotImplementedException("visit(FloatType)"); }
    default T visit(IntType integerType) { throw new NotImplementedException("visit(IntType)"); }
    default T visit(PointerType pointerType) { throw new NotImplementedException("visit(PointerType)"); }
    default T visit(BooleanType booleanType) { throw new NotImplementedException("visit(BooleanType)"); }
    default T visit(UnknownType unknownType) { throw new NotImplementedException("visit(UnknownType)"); }
    default T visit(ReferenceType referenceType) { throw new NotImplementedException("visit(ReferenceType)"); }
    default T visit(CharacterType characterType) { throw new NotImplementedException("visit(CharacterType)"); }
    default T visit(GenericUserType genericUserType) { throw new NotImplementedException("visit(GenericUserType)"); }
    default T visit(UserType userType) { throw new NotImplementedException("visit(UserType)"); }
    
    // Comparisons
    default T visit(BinaryComparison binaryComparison) { throw new NotImplementedException("visit(BinaryComparison)"); }
    default T visit(ThreeWayComparisonOp threeWayComparisonOp) { throw new NotImplementedException("visit(ThreeWayComparisonOp)"); }
} 
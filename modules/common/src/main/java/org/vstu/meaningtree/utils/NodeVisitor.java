package org.vstu.meaningtree.utils;

import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.definitions.*;
import org.vstu.meaningtree.nodes.expressions.*;
import org.vstu.meaningtree.nodes.expressions.bitwise.*;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SelfReference;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.*;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.newexpr.*;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.calls.*;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerPackOp;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerUnpackOp;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.io.PrintValues;
import org.vstu.meaningtree.nodes.modules.*;
import org.vstu.meaningtree.nodes.statements.*;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.SwitchStatement;
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
    default T visit(UnmodifiableListLiteral unmodifiableListLiteral) { throw new NotImplementedException("visit(UnmodifiableListLiteral)"); }
    default T visit(ListLiteral listLiteral) { throw new NotImplementedException("visit(ListLiteral)"); }
    default T visit(InterpolatedStringLiteral interpolatedStringLiteral) { throw new NotImplementedException("visit(InterpolatedStringLiteral)"); }
    default T visit(FloatLiteral floatLiteral) { throw new NotImplementedException("visit(FloatLiteral)"); }
    default T visit(IntegerLiteral integerLiteral) { throw new NotImplementedException("visit(IntegerLiteral)"); }
    default T visit(StringLiteral stringLiteral) { throw new NotImplementedException("visit(StringLiteral)"); }
    default T visit(SelfReference selfReference) { throw new NotImplementedException("visit(SelfReference)"); }
    default T visit(UnaryMinusOp unaryMinusOp) { throw new NotImplementedException("visit(UnaryMinusOp)"); }
    default T visit(UnaryPlusOp unaryPlusOp) { throw new NotImplementedException("visit(UnaryPlusOp)"); }
    default T visit(AddOp addOp) { throw new NotImplementedException("visit(AddOp)"); }
    default T visit(SubOp subOp) { throw new NotImplementedException("visit(SubOp)"); }
    default T visit(MulOp mulOp) { throw new NotImplementedException("visit(MulOp)"); }
    default T visit(DivOp divOp) { throw new NotImplementedException("visit(DivOp)"); }
    default T visit(ModOp modOp) { throw new NotImplementedException("visit(ModOp)"); }
    default T visit(FloorDivOp floorDivOp) { throw new NotImplementedException("visit(FloorDivOp)"); }
    default T visit(EqOp eqOp) { throw new NotImplementedException("visit(EqOp)"); }
    default T visit(GeOp geOp) { throw new NotImplementedException("visit(GeOp)"); }
    default T visit(GtOp gtOp) { throw new NotImplementedException("visit(GtOp)"); }
    default T visit(LeOp leOp) { throw new NotImplementedException("visit(LeOp)"); }
    default T visit(LtOp ltOp) { throw new NotImplementedException("visit(LtOp)"); }
    default T visit(NotEqOp notEqOp) { throw new NotImplementedException("visit(NotEqOp)"); }
    default T visit(ShortCircuitAndOp shortCircuitAndOp) { throw new NotImplementedException("visit(ShortCircuitAndOp)"); }
    default T visit(ShortCircuitOrOp shortCircuitOrOp) { throw new NotImplementedException("visit(ShortCircuitOrOp)"); }
    default T visit(NotOp notOp) { throw new NotImplementedException("visit(NotOp)"); }
    default T visit(ParenthesizedExpression parenthesizedExpression) { throw new NotImplementedException("visit(ParenthesizedExpression)"); }
    default T visit(AssignmentExpression assignmentExpression) { throw new NotImplementedException("visit(AssignmentExpression)"); }
    default T visit(AssignmentStatement assignmentStatement) { throw new NotImplementedException("visit(AssignmentStatement)"); }
    default T visit(CompoundStatement compoundStatement) { throw new NotImplementedException("visit(CompoundStatement)"); }
    default T visit(ExpressionStatement expressionStatement) { throw new NotImplementedException("visit(ExpressionStatement)"); }
    default T visit(SimpleIdentifier simpleIdentifier) { throw new NotImplementedException("visit(SimpleIdentifier)"); }
    default T visit(IfStatement ifStatement) { throw new NotImplementedException("visit(IfStatement)"); }
    default T visit(GeneralForLoop generalForLoop) { throw new NotImplementedException("visit(GeneralForLoop)"); }
    default T visit(CompoundComparison compoundComparison) { throw new NotImplementedException("visit(CompoundComparison)"); }
    default T visit(RangeForLoop rangeForLoop) { throw new NotImplementedException("visit(RangeForLoop)"); }
    default T visit(ProgramEntryPoint programEntryPoint) { throw new NotImplementedException("visit(ProgramEntryPoint)"); }
    default T visit(MethodCall methodCall) { throw new NotImplementedException("visit(MethodCall)"); }
    default T visit(PrintValues printValues) { throw new NotImplementedException("visit(PrintValues)"); }
    default T visit(FunctionCall functionCall) { throw new NotImplementedException("visit(FunctionCall)"); }
    default T visit(WhileLoop whileLoop) { throw new NotImplementedException("visit(WhileLoop)"); }
    default T visit(ScopedIdentifier scopedIdentifier) { throw new NotImplementedException("visit(ScopedIdentifier)"); }
    default T visit(PostfixIncrementOp postfixIncrementOp) { throw new NotImplementedException("visit(PostfixIncrementOp)"); }
    default T visit(PostfixDecrementOp postfixDecrementOp) { throw new NotImplementedException("visit(PostfixDecrementOp)"); }
    default T visit(PrefixIncrementOp prefixIncrementOp) { throw new NotImplementedException("visit(PrefixIncrementOp)"); }
    default T visit(PrefixDecrementOp prefixDecrementOp) { throw new NotImplementedException("visit(PrefixDecrementOp)"); }
    default T visit(PowOp powOp) { throw new NotImplementedException("visit(PowOp)"); }
    default T visit(PackageDeclaration packageDeclaration) { throw new NotImplementedException("visit(PackageDeclaration)"); }
    default T visit(ClassDeclaration classDeclaration) { throw new NotImplementedException("visit(ClassDeclaration)"); }
    default T visit(ClassDefinition classDefinition) { throw new NotImplementedException("visit(ClassDefinition)"); }
    default T visit(Comment comment) { throw new NotImplementedException("visit(Comment)"); }
    default T visit(BreakStatement breakStatement) { throw new NotImplementedException("visit(BreakStatement)"); }
    default T visit(ContinueStatement continueStatement) { throw new NotImplementedException("visit(ContinueStatement)"); }
    default T visit(ObjectConstructorDefinition objectConstructorDefinition) { throw new NotImplementedException("visit(ObjectConstructorDefinition)"); }
    default T visit(MethodDefinition methodDefinition) { throw new NotImplementedException("visit(MethodDefinition)"); }
    default T visit(SwitchStatement switchStatement) { throw new NotImplementedException("visit(SwitchStatement)"); }
    default T visit(NullLiteral nullLiteral) { throw new NotImplementedException("visit(NullLiteral)"); }
    default T visit(StaticImportAll staticImportAll) { throw new NotImplementedException("visit(StaticImportAll)"); }
    default T visit(StaticImportMembers staticImportMembers) { throw new NotImplementedException("visit(StaticImportMembers)"); }
    default T visit(ImportAll importAll) { throw new NotImplementedException("visit(ImportAll)"); }
    default T visit(ImportMembers importMembers) { throw new NotImplementedException("visit(ImportMembers)"); }
    default T visit(UserType userType) { throw new NotImplementedException("visit(UserType)"); }
    default T visit(ObjectNewExpression objectNewExpression) { throw new NotImplementedException("visit(ObjectNewExpression)"); }
    default T visit(BoolLiteral boolLiteral) { throw new NotImplementedException("visit(BoolLiteral)"); }
    default T visit(MemberAccess memberAccess) { throw new NotImplementedException("visit(MemberAccess)"); }
    default T visit(ArrayNewExpression arrayNewExpression) { throw new NotImplementedException("visit(ArrayNewExpression)"); }
    default T visit(ArrayInitializer arrayInitializer) { throw new NotImplementedException("visit(ArrayInitializer)"); }
    default T visit(ReturnStatement returnStatement) { throw new NotImplementedException("visit(ReturnStatement)"); }
    default T visit(CastTypeExpression castTypeExpression) { throw new NotImplementedException("visit(CastTypeExpression)"); }
    default T visit(IndexExpression indexExpression) { throw new NotImplementedException("visit(IndexExpression)"); }
    default T visit(TernaryOperator ternaryOperator) { throw new NotImplementedException("visit(TernaryOperator)"); }
    default T visit(BitwiseAndOp bitwiseAndOp) { throw new NotImplementedException("visit(BitwiseAndOp)"); }
    default T visit(BitwiseOrOp bitwiseOrOp) { throw new NotImplementedException("visit(BitwiseOrOp)"); }
    default T visit(XorOp xorOp) { throw new NotImplementedException("visit(XorOp)"); }
    default T visit(InversionOp inversionOp) { throw new NotImplementedException("visit(InversionOp)"); }
    default T visit(LeftShiftOp leftShiftOp) { throw new NotImplementedException("visit(LeftShiftOp)"); }
    default T visit(RightShiftOp rightShiftOp) { throw new NotImplementedException("visit(RightShiftOp)"); }
    default T visit(MultipleAssignmentStatement multipleAssignmentStatement) { throw new NotImplementedException("visit(MultipleAssignmentStatement)"); }
    default T visit(InfiniteLoop infiniteLoop) { throw new NotImplementedException("visit(InfiniteLoop)"); }
    default T visit(ExpressionSequence expressionSequence) { throw new NotImplementedException("visit(ExpressionSequence)"); }
    default T visit(CharacterLiteral characterLiteral) { throw new NotImplementedException("visit(CharacterLiteral)"); }
    default T visit(DoWhileLoop doWhileLoop) { throw new NotImplementedException("visit(DoWhileLoop)"); }
    default T visit(PointerPackOp pointerPackOp) { throw new NotImplementedException("visit(PointerPackOp)"); }
    default T visit(PointerUnpackOp pointerUnpackOp) { throw new NotImplementedException("visit(PointerUnpackOp)"); }
    default T visit(PointerType pointerType) { throw new NotImplementedException("visit(PointerType)"); }
    default T visit(ReferenceType referenceType) { throw new NotImplementedException("visit(ReferenceType)"); }
}
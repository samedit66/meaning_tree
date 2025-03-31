package org.vstu.meaningtree.languages;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.languages.utils.HindleyMilner;
import org.vstu.meaningtree.languages.utils.Scope;
import org.vstu.meaningtree.nodes.*;
import org.vstu.meaningtree.nodes.declarations.ClassDeclaration;
import org.vstu.meaningtree.nodes.declarations.FieldDeclaration;
import org.vstu.meaningtree.nodes.declarations.MethodDeclaration;
import org.vstu.meaningtree.nodes.declarations.VariableDeclaration;
import org.vstu.meaningtree.nodes.declarations.components.DeclarationArgument;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.definitions.ObjectConstructorDefinition;
import org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator;
import org.vstu.meaningtree.nodes.enums.DeclarationModifier;
import org.vstu.meaningtree.nodes.expressions.BinaryExpression;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.ParenthesizedExpression;
import org.vstu.meaningtree.nodes.expressions.bitwise.*;
import org.vstu.meaningtree.nodes.expressions.calls.FunctionCall;
import org.vstu.meaningtree.nodes.expressions.calls.MethodCall;
import org.vstu.meaningtree.nodes.expressions.comparison.*;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SelfReference;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.*;
import org.vstu.meaningtree.nodes.expressions.logical.NotOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitAndOp;
import org.vstu.meaningtree.nodes.expressions.logical.ShortCircuitOrOp;
import org.vstu.meaningtree.nodes.expressions.math.*;
import org.vstu.meaningtree.nodes.expressions.newexpr.ArrayNewExpression;
import org.vstu.meaningtree.nodes.expressions.newexpr.ObjectNewExpression;
import org.vstu.meaningtree.nodes.expressions.other.*;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerPackOp;
import org.vstu.meaningtree.nodes.expressions.pointers.PointerUnpackOp;
import org.vstu.meaningtree.nodes.expressions.unary.*;
import org.vstu.meaningtree.nodes.interfaces.HasInitialization;
import org.vstu.meaningtree.nodes.io.PrintValues;
import org.vstu.meaningtree.nodes.modules.*;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.ExpressionSequence;
import org.vstu.meaningtree.nodes.statements.ExpressionStatement;
import org.vstu.meaningtree.nodes.statements.ReturnStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.assignments.MultipleAssignmentStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;
import org.vstu.meaningtree.nodes.statements.conditions.SwitchStatement;
import org.vstu.meaningtree.nodes.statements.conditions.components.*;
import org.vstu.meaningtree.nodes.statements.loops.*;
import org.vstu.meaningtree.nodes.statements.loops.control.BreakStatement;
import org.vstu.meaningtree.nodes.statements.loops.control.ContinueStatement;
import org.vstu.meaningtree.nodes.types.NoReturn;
import org.vstu.meaningtree.nodes.types.UnknownType;
import org.vstu.meaningtree.nodes.types.UserType;
import org.vstu.meaningtree.nodes.types.builtin.*;
import org.vstu.meaningtree.nodes.types.containers.ArrayType;
import org.vstu.meaningtree.nodes.types.containers.components.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.vstu.meaningtree.nodes.enums.AugmentedAssignmentOperator.POW;

public class JavaViewer extends LanguageViewer {

    private final String _indentation;
    private int _indentLevel;
    private final boolean _openBracketOnSameLine;
    private final boolean _bracketsAroundCaseBranches;
    private final boolean _autoVariableDeclaration;

    private Scope _currentScope;
    private Scope _typeScope;

    private void enterNewScope() {
        _currentScope = new Scope(_currentScope);
        _typeScope = new Scope(_typeScope);
    }

    private void leaveScope() {
        Scope parentScope = _currentScope.getParentScope();
        Scope parentTypeScope = _typeScope.getParentScope();
        if (parentScope == null) {
            throw new RuntimeException("No parent scope found");
        }
        _currentScope = parentScope;
        _typeScope = parentTypeScope;
    }

    private void addVariableToCurrentScope(@NotNull SimpleIdentifier variableName, Type type) {
        _currentScope.addVariable(variableName, type);
    }

    private void addMethodToCurrentScope(@NotNull SimpleIdentifier methodName, Type returnType) {
        _currentScope.addMethod(methodName, returnType);
    }

    public JavaViewer(int indentSpaceCount,
                      boolean openBracketOnSameLine,
                      boolean bracketsAroundCaseBranches,
                      boolean autoVariableDeclaration
    ) {
        _indentation = " ".repeat(indentSpaceCount);
        _indentLevel = 0;
        _openBracketOnSameLine = openBracketOnSameLine;
        _bracketsAroundCaseBranches = bracketsAroundCaseBranches;
        _currentScope = new Scope();
        _typeScope = new Scope();
        _autoVariableDeclaration = autoVariableDeclaration;
    }

    public JavaViewer() { this(4, true, false, false); }

    @Override
    public String visit(Node node) {
        Objects.requireNonNull(node);

        if (node instanceof Expression expression) {
            HindleyMilner.inference(expression, _typeScope);
        }
        else if (node instanceof Statement statement) {
            HindleyMilner.inference(statement, _typeScope);
        }

        return switch (node) {
            case UnmodifiableListLiteral unmodifiableListLiteral -> visit(unmodifiableListLiteral);
            case ListLiteral listLiteral -> visit(listLiteral);
            case InterpolatedStringLiteral interpolatedStringLiteral -> visit(interpolatedStringLiteral);
            case FloatLiteral l -> visit(l);
            case IntegerLiteral l -> visit(l);
            case StringLiteral l -> visit(l);
            case SelfReference selfReference -> visit(selfReference);
            case UnaryMinusOp unaryMinusOp -> visit(unaryMinusOp);
            case UnaryPlusOp unaryPlusOp -> visit(unaryPlusOp);
            case AddOp op -> visit(op);
            case SubOp op -> visit(op);
            case MulOp op -> visit(op);
            case DivOp op -> visit(op);
            case ModOp op -> visit(op);
            case FloorDivOp op -> visit(op);
            case EqOp op -> visit(op);
            case GeOp op -> visit(op);
            case GtOp op -> visit(op);
            case LeOp op -> visit(op);
            case LtOp op -> visit(op);
            case NotEqOp op -> visit(op);
            case ShortCircuitAndOp op -> visit(op);
            case ShortCircuitOrOp op -> visit(op);
            case NotOp op -> visit(op);
            case ParenthesizedExpression expr -> visit(expr);
            case AssignmentExpression expr -> visit(expr);
            case AssignmentStatement stmt -> visit(stmt);
            case FieldDeclaration decl -> visit(decl);
            case VariableDeclaration stmt -> visit(stmt);
            case CompoundStatement stmt -> visit(stmt);
            case ExpressionStatement stmt -> visit(stmt);
            case SimpleIdentifier expr -> visit(expr);
            case IfStatement stmt -> visit(stmt);
            case GeneralForLoop stmt -> visit(stmt);
            case CompoundComparison cmp -> visit(cmp);
            case RangeForLoop rangeLoop -> visit(rangeLoop);
            case ProgramEntryPoint entryPoint -> visit(entryPoint);
            case MethodCall methodCall -> visit(methodCall);
            case PrintValues printValues -> visit(printValues);
            case FunctionCall funcCall -> visit(funcCall);
            case WhileLoop whileLoop -> visit(whileLoop);
            case ScopedIdentifier scopedIdent -> visit(scopedIdent);
            case PostfixIncrementOp inc -> visit(inc);
            case PostfixDecrementOp dec -> visit(dec);
            case PrefixIncrementOp inc -> visit(inc);
            case PrefixDecrementOp dec -> visit(dec);
            case PowOp op -> visit(op);
            case PackageDeclaration decl -> visit(decl);
            case ClassDeclaration decl -> visit(decl);
            case ClassDefinition def -> visit(def);
            case Comment comment -> visit(comment);
            case BreakStatement stmt -> visit(stmt);
            case ContinueStatement stmt -> visit(stmt);
            case ObjectConstructorDefinition objectConstructor -> visit(objectConstructor);
            case MethodDefinition methodDefinition -> visit(methodDefinition);
            case SwitchStatement switchStatement -> visit(switchStatement);
            case NullLiteral nullLiteral -> visit(nullLiteral);
            case StaticImportAll staticImportAll -> visit(staticImportAll);
            case StaticImportMembers staticImportMembers -> visit(staticImportMembers);
            case ImportAll importAll -> visit(importAll);
            case ImportMembers importMembers -> visit(importMembers);
            case UserType userType -> visit(userType);
            case ObjectNewExpression objectNewExpression -> visit(objectNewExpression);
            case BoolLiteral boolLiteral -> visit(boolLiteral);
            case MemberAccess memberAccess -> visit(memberAccess);
            case ArrayNewExpression arrayNewExpression -> visit(arrayNewExpression);
            case ArrayInitializer arrayInitializer -> visit(arrayInitializer);
            case ReturnStatement returnStatement -> visit(returnStatement);
            case CastTypeExpression castTypeExpression -> visit(castTypeExpression);
            case IndexExpression indexExpression -> visit(indexExpression);
            case TernaryOperator ternaryOperator -> visit(ternaryOperator);
            case BitwiseAndOp bitwiseAndOp -> visit(bitwiseAndOp);
            case BitwiseOrOp bitwiseOrOp -> visit(bitwiseOrOp);
            case XorOp xorOp -> visit(xorOp);
            case InversionOp inversionOp -> visit(inversionOp);
            case LeftShiftOp leftShiftOp -> visit(leftShiftOp);
            case RightShiftOp rightShiftOp -> visit(rightShiftOp);
            case MultipleAssignmentStatement multipleAssignmentStatement -> visit(multipleAssignmentStatement);
            case InfiniteLoop infiniteLoop -> visit(infiniteLoop);
            case ExpressionSequence expressionSequence -> visit(expressionSequence);
            case CharacterLiteral characterLiteral -> visit(characterLiteral);
            case DoWhileLoop doWhileLoop -> visit(doWhileLoop);
            case PointerPackOp ptr -> visit(ptr.getArgument());
            case PointerUnpackOp ptr -> visit(ptr.getArgument());
            case PointerType ptr -> visit(ptr.getTargetType());
            case ReferenceType ref -> visit(ref.getTargetType());
            default -> throw new IllegalStateException(String.format("Can't stringify node %s", node.getClass()));
        };
    }

    public String visit(UnmodifiableListLiteral unmodifiableListLiteral) {
        var builder = new StringBuilder();
        builder.append("new Object[] {");

        for (Expression expression : unmodifiableListLiteral.getList()) {
            builder.append(visit(expression)).append(", ");
        }

        if (builder.length() > 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }

    public String visit(ListLiteral listLiteral) {
        var builder = new StringBuilder();
        builder.append("new Object[] {");

        for (Expression expression : listLiteral.getList()) {
            builder.append(visit(expression)).append(", ");
        }

        if (builder.length() > 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }

    public String visit(InterpolatedStringLiteral interpolatedStringLiteral) {
        var builder = new StringBuilder();
        var argumentsBuilder = new StringBuilder();

        builder.append("String.format(\"");
        for (Expression stringPart : interpolatedStringLiteral) {
            Type exprType = HindleyMilner.inference(stringPart, _typeScope);
            switch (exprType) {
                case StringType stringType -> {
                    var string = visit(stringPart);
                    builder.append(string, 1, string.length() - 1);
                }
                case IntType integerType -> {
                    builder.append("%d");
                    argumentsBuilder.append(visit(stringPart)).append(", ");
                }
                case FloatType floatType -> {
                    builder.append("%f");
                    argumentsBuilder.append(visit(stringPart)).append(", ");
                }
                default -> {
                    builder.append("%s");
                    argumentsBuilder.append(visit(stringPart)).append(", ");
                }
            }
        }
        builder.append("\"");

        if (argumentsBuilder.length() > 2) {
            argumentsBuilder.deleteCharAt(argumentsBuilder.length() - 1);
            argumentsBuilder.deleteCharAt(argumentsBuilder.length() - 1);

            builder
                    .append(", ")
                    .append(argumentsBuilder.toString());
        }

        builder.append(")");
        return builder.toString();
    }

    public String visit(PrintValues printValues) {
        StringBuilder builder = new StringBuilder();

        builder.append("System.out.");
        builder.append(printValues.addsNewLine() ? "println" : "print");
        builder.append("(");

        if (printValues.valuesCount() > 1) {
            builder.append("String.join(");

            if (printValues.separator != null) {
                builder
                        .append(visit(printValues.separator))
                        .append(", ");
            }

            for (Expression value : printValues.getArguments()) {
                builder
                        .append(visit(value))
                        .append(", ");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);

            if (!printValues.addsNewLine() && printValues.end != null) {
                builder.append(visit(printValues.end));
            }

            builder.append(")");
        }
        else if (printValues.valuesCount() == 1) {
            builder.append(
                    visit(printValues.getArguments().getFirst())
            );
        }

        builder.append(")");

        return builder.toString();
    }

    public String visit(UnaryPlusOp unaryPlusOp) {
        return "+" + visit(unaryPlusOp.getArgument());
    }

    public String visit(UnaryMinusOp unaryMinusOp) {
        return "-" + visit(unaryMinusOp.getArgument());
    }

    public String visit(DoWhileLoop doWhileLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append("do");

        if (_openBracketOnSameLine) {
            builder.append(" {\n");
        }
        else {
            builder.append("\n").append(indent("{\n"));
        }

        List<Node> nodes = new ArrayList<>();
        Statement body = doWhileLoop.getBody();
        if (body instanceof CompoundStatement) {
            nodes.addAll(Arrays.asList(((CompoundStatement) body).getNodes()));
        }
        else {
            nodes.add(body);
        }

        increaseIndentLevel();
        for (Node node : nodes) {
            builder
                    .append(indent(visit(node)))
                    .append("\n");
        }
        decreaseIndentLevel();

        if (_openBracketOnSameLine) {
            builder.append("} ");
        }
        else {
            builder.append("}\n");
        }

        builder.append(
                "while %s;".formatted(
                        visit(doWhileLoop.getCondition())
                )
        );

        return builder.toString();
    }

    public String visit(CharacterLiteral characterLiteral) {
        String symbol = StringEscapeUtils.escapeJava(
                Character.toString(characterLiteral.getValue())
        );
        return "'" + symbol + "'";
    }

    public String visit(ExpressionSequence expressionSequence) {
         StringBuilder builder = new StringBuilder();

         for (Expression expression : expressionSequence.getExpressions()) {
             builder.append(visit(expression)).append(", ");
         }

         // Удаляем лишние пробел и запятую
         if (builder.length() > 2) {
             builder.deleteCharAt(builder.length() - 1);
             builder.deleteCharAt(builder.length() - 1);
         }

         return builder.toString();
    }

    public String visit(InfiniteLoop infiniteLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append(indent("while (true)"));
        Statement body = infiniteLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(visit(compoundStatement));
            }
            else {
                builder.append("\n");
                builder.append(indent(visit(body)));
            }
        }
        else {
            builder.append("\n");
            increaseIndentLevel();
            builder.append(indent(visit(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    public String visit(SelfReference selfReference) {
        return "this";
    }

    public String visit(ObjectConstructorDefinition objectConstructor) {
        MethodDeclaration constructorDeclaration =
                (MethodDeclaration) objectConstructor.getDeclaration();

        StringBuilder builder = new StringBuilder();

        String modifiers = visit(constructorDeclaration.getModifiers());
        if (!modifiers.isEmpty()) {
            builder.append(modifiers).append(" ");
        }

        String name = visit(objectConstructor.getName());
        builder.append(name);

        String parameters = toStringParameters(constructorDeclaration.getArguments());
        if (!parameters.isEmpty()) {
            builder.append(parameters);
        }

        String body = visit(objectConstructor.getBody());
        if (_openBracketOnSameLine)
            { builder.append(" ").append(body); }
        else
            { builder.append("\n").append(indent(body)); }

        return builder.toString();
    }

    public String visit(MultipleAssignmentStatement multipleAssignmentStatement) {
        StringBuilder builder = new StringBuilder();

        for (AssignmentStatement stmt : multipleAssignmentStatement.getStatements()) {
            builder.append(visit(stmt)).append("\n");
        }

        // Удаляем последний перевод строки
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    public String visit(RightShiftOp rightShiftOp) {
        return visit(rightShiftOp, ">>");
    }

    public String visit(LeftShiftOp leftShiftOp) {
        return visit(leftShiftOp, "<<");
    }

    public String visit(InversionOp inversionOp) {
        return "~" + visit(inversionOp.getArgument());
    }

    public String visit(XorOp xorOp) {
        return visit(xorOp, "^");
    }

    public String visit(BitwiseOrOp bitwiseOrOp) {
        return visit(bitwiseOrOp, "|");
    }

    public String visit(BitwiseAndOp bitwiseAndOp) {
        return visit(bitwiseAndOp, "&");
    }

    public String visit(TernaryOperator ternaryOperator) {
        String condition = visit(ternaryOperator.getCondition());
        String consequence = visit(ternaryOperator.getThenExpr());
        String alternative = visit(ternaryOperator.getElseExpr());
        return "%s ? %s : %s".formatted(condition, consequence, alternative);
    }

    public String visit(IndexExpression indexExpression) {
        Expression arrayName = indexExpression.getExpr();
        String name = visit(arrayName);
        String index = visit(indexExpression.getIndex());
        return "%s[%s]".formatted(name, index);
    }

    public String visit(CastTypeExpression castTypeExpression) {
        String castType = visit(castTypeExpression.getCastType());
        String value = visit(castTypeExpression.getValue());
        return "(%s) %s".formatted(castType, value);
    }

    public String visit(ReturnStatement returnStatement) {
        Expression expression = returnStatement.getExpression();
        return (expression != null) ? "return %s;".formatted(visit(expression)) : "return;";
    }

    public String visit(ArrayInitializer initializer) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        List<Expression> values = initializer.getValues();
        for (Expression value : values) {
            builder
                    .append(visit(value))
                    .append(", ");
        }

        if (builder.length() > 1) {
            // Удаляем лишние пробел и запятую
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");
        return builder.toString();
    }

    public String visit(ArrayNewExpression arrayNewExpression) {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");

        String type = visit(arrayNewExpression.getType());
        builder.append(type);

        String dimensions = visit(arrayNewExpression.getShape());
        builder.append(dimensions);

        ArrayInitializer optionalInitializer = arrayNewExpression.getInitializer();
        if (optionalInitializer != null) {
            String initializer = visit(optionalInitializer);
            builder.append(" ").append(initializer);
        }

        return builder.toString();
    }

    public String visit(MemberAccess memberAccess) {
        String object = visit(memberAccess.getExpression());
        String member = visit(memberAccess.getMember());
        return "%s.%s".formatted(object, member);
    }

    public String visit(BoolLiteral boolLiteral) {
        return boolLiteral.getValue() ? "true" : "false";
    }

    public String visit(ObjectNewExpression objectNewExpression) {
        String typeName = visit(objectNewExpression.getType());

        String arguments = objectNewExpression
                .getConstructorArguments()
                .stream()
                .map(this::visit)
                .collect(Collectors.joining(", "));

        return "new %s(%s)".formatted(typeName, arguments);
    }

    public String visit(MethodCall methodCall) {
        String object = visit(methodCall.getObject());
        String methodName = visit(methodCall.getFunctionName());

        String arguments = methodCall
                .getArguments()
                .stream()
                .map(this::visit)
                .collect(Collectors.joining(", "));

        return "%s.%s(%s)".formatted(object, methodName, arguments);
    }

    public String visit(UserType userType) {
        return visit(userType.getName());
    }

    public String visit(StaticImportAll staticImportAll) {
        String importTemplate = "import static %s.*;";
        return importTemplate.formatted(visit(staticImportAll.getScope()));
    }

    public String visit(StaticImportMembers staticImportMembers) {
        StringBuilder builder = new StringBuilder();

        String importTemplate = "import static %s.%s;";
        for (Identifier member : staticImportMembers.getMembers()) {
            builder
                    .append(
                            importTemplate.formatted(
                                    visit(staticImportMembers.getScope()),
                                    visit(member)
                            )
                    )
                    .append("\n");
            ;
        }

        // Удаляем последний символ перевода строки
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public String visit(ImportAll importAll) {
        String importTemplate = "import %s.*;";
        return importTemplate.formatted(visit(importAll.getScope()));
    }

    public String visit(ImportMembers importMembers) {
        StringBuilder builder = new StringBuilder();

        String importTemplate = "import %s.%s;";
        for (Identifier member : importMembers.getMembers()) {
            builder
                    .append(
                        importTemplate.formatted(
                            visit(importMembers.getScope()),
                            visit(member)
                        )
                    )
                    .append("\n");
            ;
        }

        // Удаляем последний символ перевода строки
        if (!importMembers.getMembers().isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    public String visit(NullLiteral nullLiteral) {
        return "null";
    }

    private String toStringCaseBlock(CaseBlock caseBlock) {
        StringBuilder builder = new StringBuilder();

        Statement caseBlockBody;
        if (caseBlock instanceof MatchValueCaseBlock mvcb) {
            builder.append("case ");
            builder.append(visit(mvcb.getMatchValue()));
            builder.append(":");
            caseBlockBody = mvcb.getBody();
        }
        else if (caseBlock instanceof DefaultCaseBlock dcb) {
            builder.append("default:");
            caseBlockBody = dcb.getBody();
        }
        else {
            throw new IllegalStateException("Unsupported case block type: " + caseBlock.getClass());
        }

        List<Node> nodesList;
        if (caseBlockBody instanceof CompoundStatement compoundStatement) {
            nodesList = Arrays.asList(compoundStatement.getNodes());
        }
        else {
            nodesList = List.of(caseBlockBody);
        }

        // Внутри case веток нельзя объявлять переменные, нужно обернуть их скобками,
        // поэтому проверяем наличие деклараций переменных
        boolean hasDeclarationInside = false;
        for (Node node : nodesList) {
            if (node instanceof VariableDeclaration) {
                hasDeclarationInside = true;
                break;
            }
        }

        if (!nodesList.isEmpty()) {
            if (_bracketsAroundCaseBranches || hasDeclarationInside) {
                if (_openBracketOnSameLine) {
                    builder.append(" {\n");
                }
                else {
                    builder.append("\n").append(indent("{\n"));
                }
            }
            else {
                builder.append("\n");
            }

            increaseIndentLevel();

            for (Node node : nodesList) {
                builder
                        .append(indent(visit(node)))
                        .append("\n");
            }

            if (caseBlock instanceof BasicCaseBlock || caseBlock instanceof DefaultCaseBlock) {
                builder.append(indent("break;"));
            }
            else {
                builder.deleteCharAt(builder.length() - 1);
            }

            decreaseIndentLevel();

            if (_bracketsAroundCaseBranches || hasDeclarationInside) {
                builder
                        .append("\n")
                        .append(indent("}"));
            }
        }

        return builder.toString();
    }

    public String visit(SwitchStatement switchStatement) {
        StringBuilder builder = new StringBuilder();

        builder.append("switch (");
        builder.append(visit(switchStatement.getTargetExpression()));
        builder.append(") ");

        if (_openBracketOnSameLine) {
            builder.append("{\n");
        }
        else {
            builder.append("\n").append(indent("{\n"));
        }

        increaseIndentLevel();
        for (CaseBlock caseBlock : switchStatement.getCases()) {
            builder
                    .append(indent(toStringCaseBlock(caseBlock)))
                    .append("\n");
        }
        decreaseIndentLevel();

        builder.append(indent("}"));
        return builder.toString();
    }

    private String visit(DeclarationArgument parameter) {
        String type = visit(parameter.getType());
        String name = visit(parameter.getName());
        return "%s %s".formatted(type, name);
    }

    // В отличие от всех остальных методов, данный называется так,
    // чтобы избежать конфликтов с другими методами:
    // toStringParameters(List<Modifier> modifiers)
    // и toStringParameters(List<DeclarationArgument> parameters)
    // с точки зрения Java один и тот же тип...
    private String toStringParameters(List<DeclarationArgument> parameters) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");

        int i;
        for (i = 0; i < parameters.size(); i++) {
            DeclarationArgument parameter = parameters.get(i);
            builder.append("%s, ".formatted(visit(parameter)));
        }

        // Удаляем последний пробел и запятую, если был хотя бы один параметр
        if (i > 0) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append(")");
        return builder.toString();
    }

    private String visit(MethodDeclaration methodDeclaration) {
        StringBuilder builder = new StringBuilder();

        String modifiersList = visit(methodDeclaration.getModifiers());
        if (!modifiersList.isEmpty()) {
            builder.append(modifiersList).append(" ");
        }

        String returnType = visit(methodDeclaration.getReturnType());
        builder.append(returnType).append(" ");

        String name = visit(methodDeclaration.getName());
        builder.append(name);

        String parameters = toStringParameters(methodDeclaration.getArguments());
        builder.append(parameters);

        return builder.toString();
    }

    public String visit(MethodDefinition methodDefinition) {
        StringBuilder builder = new StringBuilder();

        // Преобразование типа нужно, чтобы избежать вызова toString(Node node)
        String methodDeclaration = visit((MethodDeclaration) methodDefinition.getDeclaration());
        builder.append(methodDeclaration);

        String body = visit(methodDefinition.getBody());
        if (_openBracketOnSameLine)
            { builder.append(" ").append(body); }
        else
            { builder.append("\n").append(indent(body)); }

        return builder.toString();
    }

    public String visit(ContinueStatement stmt) {
        return "continue;";
    }

    public String visit(BreakStatement stmt) {
        return "break;";
    }

    public String visit(Comment comment) {
        if (comment.isMultiline()) {
            return "/*" + comment.getUnescapedContent() + "*/";
        }

        return "//%s".formatted(comment.getUnescapedContent());
    }

    private String visit(FieldDeclaration decl) {
        StringBuilder builder = new StringBuilder();

        String modifiers = visit(decl.getModifiers());
        builder.append(modifiers);
        // Добавляем пробел в конце, если есть хотя бы один модификатор
        if (!builder.isEmpty()) {
            builder.append(" ");
        }

        VariableDeclaration variableDeclaration = new VariableDeclaration(decl.getType(), decl.getDeclarators());
        builder.append(visit(variableDeclaration));

        return builder.toString();
    }

    private String visit(List<DeclarationModifier> modifiers) {
        StringBuilder builder = new StringBuilder();

        for (DeclarationModifier modifier : modifiers) {
            builder.append(
                    switch (modifier) {
                        case PUBLIC -> "public";
                        case PRIVATE -> "private";
                        case PROTECTED -> "protected";
                        case ABSTRACT -> "abstract";
                        case CONST -> "final";
                        case STATIC -> "static";
                        default -> throw new IllegalArgumentException();
                    }
            ).append(" ");
        }

        // Удаляем в конце ненужный пробел, если было более одного модификатора
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    public String visit(ClassDeclaration decl) {
        String modifiers = visit(decl.getModifiers());
        if (!modifiers.isEmpty()) {
            modifiers += " ";
        }

        return modifiers + "class " + visit(decl.getName());
    }

    public String visit(ClassDefinition def) {
        StringBuilder builder = new StringBuilder();

        String declaration = visit(def.getDeclaration());
        builder.append(declaration);

        String body = visit(def.getBody());
        if (_openBracketOnSameLine)
        { builder.append(" ").append(body); }
        else
        { builder.append("\n").append(indent(body)); }

        return builder.toString();
    }

    public String visit(FloatLiteral literal) {
        String s = Double.toString(literal.getDoubleValue());
        if (!literal.isDoublePrecision()) {
            s = s.concat("f");
        }
        return s;
    }

    public String visit(IntegerLiteral literal) {
        String s = literal.getStringValue(false);
        if (literal.isLong()) {
            s = s.concat("L");
        }
        return s;
    }

    public String visit(StringLiteral literal) {
        if (literal.isMultiline()) {
            return "\"\"\"%s\"\"\"".formatted(literal.getEscapedValue());
        }

        return "\"%s\"".formatted(literal.getEscapedValue());
    }

    private String visit(BinaryExpression expr, String sign) {
        return String.format("%s %s %s", visit(expr.getLeft()), sign, visit(expr.getRight()));
    }

    public String visit(AddOp op) {
        return visit(op, "+");
    }

    public String visit(SubOp op) {
        return visit(op, "-");
    }

    public String visit(MulOp op) {
        return visit(op, "*");
    }

    public String visit(DivOp op) {
        return visit(op, "/");
    }

    public String visit(ModOp op) {
        return visit(op, "%");
    }

    public String visit(FloorDivOp op) {
        return "(int) " + visit(op, "/");
    }

    public String visit(EqOp op) {
        return visit(op, "==");
    }

    public String visit(GeOp op) {
        return visit(op, ">=");
    }

    public String visit(GtOp op) {
        return visit(op, ">");
    }

    public String visit(LeOp op) {
        return visit(op, "<=");
    }

    public String visit(LtOp op) {
        return visit(op, "<");
    }

    public String visit(NotEqOp op) {
        return visit(op, "!=");
    }

    public String visit(ShortCircuitAndOp op) {
        return visit(op, "&&");
    }

    public String visit(ShortCircuitOrOp op) {
        return visit(op, "||");
    }

    public String visit(NotOp op) {
        return String.format("!%s", visit(op.getArgument()));
    }

    public String visit(ParenthesizedExpression expr) {
        return String.format("(%s)", visit(expr.getExpression()));
    }

    private String visit(AugmentedAssignmentOperator op, Expression left, Expression right) {
        String l = visit(left);
        String r = visit(right);

        // В Java нет встроенного оператора возведения в степень, следовательно,
        // нет и соотвествующего оператора присванивания, поэтому этот случай обрабатываем по особому
        if (op == POW) {
            return "%s = Math.pow(%s, %s)".formatted(l, l, r);
        }

        String o = switch (op) {
            case NONE -> "=";
            case ADD -> "+=";
            case SUB -> "-=";
            case MUL -> "*=";
            // В Java тип деления определяется не видом операции, а типом операндов,
            // поэтому один и тот же оператор
            case DIV, FLOOR_DIV -> "/=";
            case BITWISE_AND -> "&=";
            case BITWISE_OR -> "|=";
            case BITWISE_XOR -> "^=";
            case BITWISE_SHIFT_LEFT -> "<<=";
            case BITWISE_SHIFT_RIGHT -> ">>=";
            case MOD -> "%=";
            default -> throw new IllegalStateException("Unexpected type of augmented assignment operator: " + op);
        };

        if (right instanceof IntegerLiteral integerLiteral
                && (long) integerLiteral.getValue() == 1
                && (o.equals("+=") || o.equals("-="))) {
            o = switch (o) {
                case "+=" -> "++";
                case "-=" -> "--";
                default -> throw new IllegalArgumentException();
            };

            return l + o;
        }

        return "%s %s %s".formatted(l, o, r);
    }

    public String visit(AssignmentExpression expr) {
        return visit(expr.getAugmentedOperator(), expr.getLValue(), expr.getRValue());
    }

    public String visit(AssignmentStatement stmt) {
        AugmentedAssignmentOperator assignmentOperator = stmt.getAugmentedOperator();
        Expression leftValue = stmt.getLValue();
        Expression rightValue = stmt.getRValue();

        if (leftValue instanceof SimpleIdentifier identifier
                && assignmentOperator == AugmentedAssignmentOperator.NONE) {
            Type variableType = _currentScope.getVariableType(identifier);
            // Objects.requireNonNull(variableType);

            if (variableType == null && _autoVariableDeclaration) {
                variableType = _typeScope.getVariableType(identifier);
                Objects.requireNonNull(variableType); // Никогда не будет null...

                String typeName = visit(variableType);
                String variableName = visit(identifier);
                addVariableToCurrentScope(identifier, variableType);
                return "%s %s = %s;".formatted(typeName, variableName, visit(rightValue));
            }
        }

        return "%s;".formatted(visit(assignmentOperator, leftValue, rightValue));
    }

    public String visit(Type type) {
        return switch (type) {
            case FloatType floatType -> visit(floatType);
            case IntType intType -> visit(intType);
            case BooleanType booleanType -> visit(booleanType);
            case StringType stringType -> visit(stringType);
            case NoReturn voidType -> visit(voidType);
            case UnknownType unknownType -> visit(unknownType);
            case ArrayType arrayType -> visit(arrayType);
            case UserType userType -> visit(userType);
            case CharacterType characterType -> visit(characterType);
            default -> throw new IllegalStateException("Unexpected value: " + type.getClass());
        };
    }

    private String visit(FloatType type) {
        return "double";
    }

    private String visit(IntType type) {
        return "int";
    }

    private String visit(BooleanType type) {
        return "boolean";
    }

    private String visit(StringType type) {
        return "String";
    }

    private String visit(NoReturn type) {
        return "void";
    }

    private String visit(UnknownType type) {
        return "Object";
    }

    private String visit(CharacterType type) {
        return "char";
    }

    private String visit(Shape shape) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < shape.getDimensionCount(); i++) {
            builder.append("[");

            Expression dimension = shape.getDimension(i);
            if (dimension != null) {
                builder.append(visit(dimension));
            }

            builder.append("]");
        }

        return builder.toString();
    }

    private String visit(ArrayType type) {
        StringBuilder builder = new StringBuilder();

        String baseType = visit(type.getItemType());
        builder.append(baseType);
        builder.append(visit(type.getShape()));

        return builder.toString();
    }

    private String visit(VariableDeclarator varDecl) {
        StringBuilder builder = new StringBuilder();


        SimpleIdentifier identifier = varDecl.getIdentifier();
        Type variableType = new UnknownType();
        Expression rValue = varDecl.getRValue();
        if (rValue != null) {
            variableType = HindleyMilner.inference(rValue, _typeScope);
        }

        addVariableToCurrentScope(identifier, variableType);

        String identifierName = visit(identifier);
        builder.append(identifierName);

        if (rValue != null) {
            builder.append(" = ").append(visit(rValue));
        }

        return builder.toString();
    }

    public String visit(VariableDeclaration stmt) {
        StringBuilder builder = new StringBuilder();

        Type declarationType = stmt.getType();
        String type = visit(declarationType);
        builder
                .append(type)
                .append(" ");

        for (VariableDeclarator varDecl : stmt.getDeclarators()) {
            builder.append(visit(varDecl)).append(", ");
        }
        // Чтобы избежать лишней головной боли на проверки "а последняя ли это декларация",
        // я автоматически после каждой декларации добавляю запятую и пробел,
        // но для последней декларации они не нужны, поэтому эти два символа удаляются,
        // как сделать красивее - не знаю...
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);

        builder.append(";");
        return builder.toString();
    }

    private void increaseIndentLevel() {
        _indentLevel++;
    }

    private void decreaseIndentLevel() {
        _indentLevel--;

        if (_indentLevel < 0) {
            throw new RuntimeException("Indentation level can't be less than zero");
        }
    }

    private String indent(String s) {
        if (_indentLevel == 0) {
            return s;
        }

        return _indentation.repeat(Math.max(0, _indentLevel)) + s;
    }

    public String visit(CompoundStatement stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        increaseIndentLevel();
        for (Node node : stmt) {
            String s = visit(node);
            if (s.isEmpty()) {
                continue;
            }

            s = indent(String.format("%s\n", s));
            builder.append(s);
        }
        decreaseIndentLevel();
        builder.append(indent("}"));
        return builder.toString();
    }

    public String visit(ExpressionStatement stmt) {
        return String.format("%s;", visit(stmt.getExpression()));
    }

    public String visit(SimpleIdentifier identifier) {
        return identifier.getName();
    }

    private String visit(ConditionBranch branch) {
        StringBuilder builder = new StringBuilder();

        String cond = visit(branch.getCondition());
        builder
                .append("(")
                .append(cond)
                .append(")");

        Statement body = branch.getBody();
        if (body instanceof CompoundStatement compStmt) {
            // Если телом ветки является блок кода, то необходимо определить
            // куда нужно добавить фигурные скобки и добавить само тело
            // Пример (для случая, когда скобка на той же строке):
            // if (a > b) {
            //     max = a;
            // }
            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(visit(compStmt));
            }
            else {
                builder
                        .append("\n")
                        .append(indent(visit(compStmt)));
            }
        }
        else {
            // В случае если тело ветки не блок кода, то добавляем отступ
            // и вставляем тело
            // Пример:
            // if (a > b)
            //     max = a;
            increaseIndentLevel();
            builder.append("\n").append(indent(visit(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String visit(BinaryComparison binComp) {
        return switch (binComp) {
            case EqOp op -> visit(op);
            case GeOp op -> visit(op);
            case GtOp op -> visit(op);
            case LeOp op -> visit(op);
            case LtOp op -> visit(op);
            case NotEqOp op -> visit(op);
            default -> throw new IllegalStateException("Unexpected value: " + binComp);
        };
    }

    public String visit(CompoundComparison cmp) {
        StringBuilder builder = new StringBuilder();

        for (BinaryComparison binComp : cmp.getComparisons()) {
            builder.append(visit(binComp)).append(" && ");
        }

        builder.delete(builder.length() - 4, builder.length());

        return builder.toString();
    }

    public String visit(IfStatement stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("if ");
        List<ConditionBranch> branches = stmt.getBranches();
        builder
                .append(visit(branches.getFirst()))
                .append("\n");

        for (ConditionBranch branch : branches.subList(1, branches.size())) {
            builder
                    .append(indent("else if "))
                    .append(visit(branch))
                    .append("\n");
        }

        if (stmt.hasElseBranch()) {
            builder.append(indent("else"));

            Statement elseBranch = stmt.getElseBranch();
            if (elseBranch instanceof IfStatement innerIfStmt) {
                builder
                        .append(" ")
                        .append(visit(innerIfStmt));
            }
            else if (elseBranch instanceof CompoundStatement innerCompStmt) {
                if (_openBracketOnSameLine) {
                    builder
                            .append(" ")
                            .append(visit(innerCompStmt));
                }
                else {
                    builder
                            .append("\n")
                            .append(indent(visit(innerCompStmt)));
                }
            }
            else {
                builder
                        .append("\n")
                        .append(visit(elseBranch));
            }
        }
        else {
            // Удаляем лишний перевод строки, если ветки else нет
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String visit(HasInitialization init) {
        return switch (init) {
            case AssignmentExpression expr -> visit(expr);
            case AssignmentStatement stmt -> visit(stmt);
            case VariableDeclaration decl -> visit(decl);
            case MultipleAssignmentStatement multipleAssignmentStatement -> {
                // Трансляция MultipleAssignmentStatement по умолчанию не подходит -
                // в результате будут получены присваивания, написанные через точку с запятой.
                // Поэтому вручную получаем список присваиваний и создаем правильное отображение.
                StringBuilder builder = new StringBuilder();

                for (AssignmentStatement assignmentStatement : multipleAssignmentStatement.getStatements()) {
                    AssignmentExpression assignmentExpression = new AssignmentExpression(
                            assignmentStatement.getLValue(),
                            assignmentStatement.getRValue()
                    );
                    builder
                            .append(visit(assignmentExpression))
                            .append(", ");
                }

                // Удаляем лишние пробел и запятую в конце последнего присвоения
                if (builder.length() > 2) {
                    builder.deleteCharAt(builder.length() - 1);
                    builder.deleteCharAt(builder.length() - 1);
                }

                yield builder.toString();
            }
            default -> throw new IllegalStateException("Unexpected value: " + init);
        };
    }

    public String visit(GeneralForLoop generalForLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append("for (");

        boolean addSemi = true;
        if (generalForLoop.hasInitializer()) {
            String init = visit(generalForLoop.getInitializer());
            if (init.stripTrailing().endsWith(";")) {
                addSemi = false;
            }
            builder.append(init);
        }
        if (addSemi) {
            builder.append("; ");
        }
        else {
            builder.append(" ");
        }

        if (generalForLoop.hasCondition()) {
            String condition = visit(generalForLoop.getCondition());
            builder.append(condition);
        }
        builder.append("; ");

        if (generalForLoop.hasUpdate()) {
            String update = visit(generalForLoop.getUpdate());
            builder.append(update);
        }

        Statement body = generalForLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            builder.append(")");

            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(visit(compoundStatement));
            }
            else {
                builder.append("\n");
                builder.append(indent(visit(body)));
            }
        }
        else {
            builder.append(")\n");
            increaseIndentLevel();
            builder.append(indent(visit(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String getForRangeUpdate(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRange().getType() == Range.Type.UP) {
            long stepValue = forRangeLoop.getStepValueAsLong();

            if (stepValue == 1) {
                return String.format("%s++", visit(forRangeLoop.getIdentifier()));
            }
            else {
                return String.format("%s += %d", visit(forRangeLoop.getIdentifier()), stepValue);
            }
        }
        else if (forRangeLoop.getRange().getType() == Range.Type.DOWN) {
            long stepValue = forRangeLoop.getStepValueAsLong();

            if (stepValue == 1) {
                return String.format("%s--", visit(forRangeLoop.getIdentifier()));
            }
            else {
                return String.format("%s -= %d", visit(forRangeLoop.getIdentifier()), stepValue);
            }
        }

        throw new RuntimeException("Can't determine range type in for loop");
    }

    private String getForRangeHeader(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRange().getType() == Range.Type.UP) {
            String header = "int %s = %s; %s %s %s; %s";
            String compOperator = forRangeLoop.isExcludingStop() ? "<" : "<=";
            return header.formatted(
                    visit(forRangeLoop.getIdentifier()),
                    visit(forRangeLoop.getStart()),
                    visit(forRangeLoop.getIdentifier()),
                    compOperator,
                    visit(forRangeLoop.getStop()),
                    getForRangeUpdate(forRangeLoop)
            );
        }
        else if (forRangeLoop.getRange().getType() == Range.Type.DOWN) {
            String header = "int %s = %s; %s %s %s; %s";
            String compOperator = forRangeLoop.isExcludingStop() ? ">" : ">=";
            return header.formatted(
                    visit(forRangeLoop.getIdentifier()),
                    visit(forRangeLoop.getStart()),
                    visit(forRangeLoop.getIdentifier()),
                    compOperator,
                    visit(forRangeLoop.getStop()),
                    getForRangeUpdate(forRangeLoop)
            );
        }

        throw new RuntimeException("Can't determine range type in for loop");
    }

    public String visit(RangeForLoop forRangeLoop) {
        StringBuilder builder = new StringBuilder();

        String header = "for (" + getForRangeHeader(forRangeLoop) + ")";
        builder.append(header);

        Statement body = forRangeLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(visit(compoundStatement));
            }
            else {
                builder.append("\n");
                builder.append(indent(visit(body)));
            }
        }
        else {
            builder.append("\n");
            increaseIndentLevel();
            builder.append(indent(visit(body)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String makeSimpleJavaProgram(List<Node> nodes) {
        StringBuilder builder = new StringBuilder();

        builder.append("package main;\n\n");

        builder.append("public class Main {\n\n");
        increaseIndentLevel();

        builder.append(
                indent("public static void main(String[] args) {\n")
        );
        increaseIndentLevel();

        for (Node node : nodes) {
            builder.append(
                    indent("%s\n".formatted(visit(node)))
            );
        }
        decreaseIndentLevel();

        builder.append(indent("}\n"));
        decreaseIndentLevel();

        builder.append("}");

        return builder.toString();
    }

    public String visit(ProgramEntryPoint entryPoint) {
        List<Node> nodes = entryPoint.getBody();
        for (var node : nodes) {
            if (node instanceof Statement statement) {
                HindleyMilner.inference(statement, _typeScope);
            }
        }

        var translationUnitMode = getConfigParameter("translationUnitMode");

        if (!entryPoint.hasMainClass()
                && translationUnitMode != null
                && translationUnitMode.getBooleanValue()) {
            return makeSimpleJavaProgram(nodes);
        }

        StringBuilder builder = new StringBuilder();
        for (Node node : nodes) {
            builder.append("%s\n".formatted(visit(node)));
        }

        return builder.toString();
    }

    public String visit(ScopedIdentifier scopedIdent) {
        StringBuilder builder = new StringBuilder();

        for (var ident : scopedIdent.getScopeResolution()) {
            builder.append(visit(ident)).append(".");
        }
        builder.deleteCharAt(builder.length() - 1); // Удаляем последнюю точку

        return builder.toString();
    }

    public String visit(FunctionCall funcCall) {
        StringBuilder builder = new StringBuilder();

        builder.append(visit(funcCall.getFunction())).append("(");
        for (Expression expr : funcCall.getArguments()) {
            builder.append(visit(expr)).append(", ");
        }

        if (!funcCall.getArguments().isEmpty()) {
            // Удаляем два последних символа - запятую и пробел
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(")");

        return builder.toString();
    }

    public String visit(WhileLoop whileLoop) {
        String header = "while (" + visit(whileLoop.getCondition()) + ")";

        Statement body = whileLoop.getBody();
        if (body instanceof CompoundStatement compStmt) {
            return header + (_openBracketOnSameLine ? " " : "\n") + visit(compStmt);
        }
        else {
            increaseIndentLevel();
            String result = header + "\n" + indent(visit(body));
            decreaseIndentLevel();
            return result;
        }
    }

    public String visit(PostfixIncrementOp inc) {
        return visit(inc.getArgument()) + "++";
    }

    public String visit(PostfixDecrementOp dec) {
        return visit(dec.getArgument()) + "--";
    }

    public String visit(PrefixIncrementOp inc) {
        return "++" + visit(inc.getArgument());
    }

    public String visit(PrefixDecrementOp dec) {
        return "--" + visit(dec.getArgument());
    }

    public String visit(PowOp op) {
        return "Math.pow(%s, %s)".formatted(visit(op.getLeft()), visit(op.getRight()));
    }

    public String visit(PackageDeclaration decl) {
        return "package %s;".formatted(visit(decl.getPackageName()));
    }
}

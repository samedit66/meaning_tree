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
import org.vstu.meaningtree.utils.NodeVisitor;

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
    private final NodeVisitor<String> _decoratingVisitor;

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
                      boolean autoVariableDeclaration,
                      NodeVisitor<String> decoratingVisitor
    ) {
        _indentation = " ".repeat(indentSpaceCount);
        _indentLevel = 0;
        _openBracketOnSameLine = openBracketOnSameLine;
        _bracketsAroundCaseBranches = bracketsAroundCaseBranches;
        _currentScope = new Scope();
        _typeScope = new Scope();
        _autoVariableDeclaration = autoVariableDeclaration;

        _decoratingVisitor = Objects.requireNonNullElse(decoratingVisitor, this);
    }

    public JavaViewer() {
        super();
        _indentation = " ".repeat(4);
        _indentLevel = 0;
        _openBracketOnSameLine = true;
        _bracketsAroundCaseBranches = false;
        _autoVariableDeclaration = false;
        _currentScope = new Scope();
        _typeScope = new Scope();
        _decoratingVisitor = this;
    }

    public String visit(UnmodifiableListLiteral unmodifiableListLiteral) {
        var builder = new StringBuilder();
        builder.append("new Object[] {");

        for (Expression expression : unmodifiableListLiteral.getList()) {
            builder.append(expression.accept(_decoratingVisitor)).append(", ");
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
            builder.append(expression.accept(_decoratingVisitor)).append(", ");
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
                    var string = stringPart.accept(_decoratingVisitor);
                    builder.append(string, 1, string.length() - 1);
                }
                case IntType integerType -> {
                    builder.append("%d");
                    argumentsBuilder.append(stringPart.accept(_decoratingVisitor)).append(", ");
                }
                case FloatType floatType -> {
                    builder.append("%f");
                    argumentsBuilder.append(stringPart.accept(_decoratingVisitor)).append(", ");
                }
                default -> {
                    builder.append("%s");
                    argumentsBuilder.append(stringPart.accept(_decoratingVisitor)).append(", ");
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
                        .append(printValues.separator.accept(_decoratingVisitor))
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
                builder.append(printValues.end.accept(_decoratingVisitor));
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
        return "+" + unaryPlusOp.getArgument().accept(_decoratingVisitor);
    }

    public String visit(UnaryMinusOp unaryMinusOp) {
        return "-" + unaryMinusOp.getArgument().accept(_decoratingVisitor);
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
                    .append(indent(node.accept(_decoratingVisitor)))
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
                        doWhileLoop.getCondition().accept(_decoratingVisitor)
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
             builder.append(expression.accept(_decoratingVisitor)).append(", ");
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
                        .append(compoundStatement.accept(_decoratingVisitor));
            }
            else {
                builder.append("\n");
                builder.append(indent(body.accept(_decoratingVisitor)));
            }
        }
        else {
            builder.append("\n");
            increaseIndentLevel();
            builder.append(indent(body.accept(_decoratingVisitor)));
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

        String body = objectConstructor.getBody().accept(_decoratingVisitor);
        if (_openBracketOnSameLine)
            { builder.append(" ").append(body); }
        else
            { builder.append("\n").append(indent(body)); }

        return builder.toString();
    }

    public String visit(MultipleAssignmentStatement multipleAssignmentStatement) {
        StringBuilder builder = new StringBuilder();

        for (AssignmentStatement stmt : multipleAssignmentStatement.getStatements()) {
            builder.append(stmt.accept(_decoratingVisitor)).append("\n");
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
        String condition = ternaryOperator.getCondition().accept(_decoratingVisitor);
        String consequence = ternaryOperator.getThenExpr().accept(_decoratingVisitor);
        String alternative = ternaryOperator.getElseExpr().accept(_decoratingVisitor);
        return "%s ? %s : %s".formatted(condition, consequence, alternative);
    }

    public String visit(IndexExpression indexExpression) {
        Expression arrayName = indexExpression.getExpr();
        String name = arrayName.accept(_decoratingVisitor);
        String index = indexExpression.getIndex().accept(_decoratingVisitor);
        return "%s[%s]".formatted(name, index);
    }

    public String visit(CastTypeExpression castTypeExpression) {
        String castType = castTypeExpression.getCastType().accept(_decoratingVisitor);
        String value = castTypeExpression.getValue().accept(_decoratingVisitor);
        return "(%s) %s".formatted(castType, value);
    }

    public String visit(ReturnStatement returnStatement) {
        Expression expression = returnStatement.getExpression();
        return (expression != null)
                ? "return %s;".formatted(expression.accept(_decoratingVisitor))
                : "return;";
    }

    public String visit(ArrayInitializer initializer) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        List<Expression> values = initializer.getValues();
        for (Expression value : values) {
            builder
                    .append(value.accept(_decoratingVisitor))
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

        String type = arrayNewExpression.getType().accept(_decoratingVisitor);
        builder.append(type);

        String dimensions = arrayNewExpression.getShape().accept(_decoratingVisitor);
        builder.append(dimensions);

        ArrayInitializer optionalInitializer = arrayNewExpression.getInitializer();
        if (optionalInitializer != null) {
            String initializer = optionalInitializer.accept(_decoratingVisitor);
            builder.append(" ").append(initializer);
        }

        return builder.toString();
    }

    public String visit(MemberAccess memberAccess) {
        String object = memberAccess.getExpression().accept(_decoratingVisitor);
        String member = memberAccess.getMember().accept(_decoratingVisitor);
        return "%s.%s".formatted(object, member);
    }

    public String visit(BoolLiteral boolLiteral) {
        return boolLiteral.getValue() ? "true" : "false";
    }

    public String visit(ObjectNewExpression objectNewExpression) {
        String typeName = objectNewExpression.getType().accept(_decoratingVisitor);

        String arguments = objectNewExpression
                .getConstructorArguments()
                .stream()
                .map(this::visit)
                .collect(Collectors.joining(", "));

        return "new %s(%s)".formatted(typeName, arguments);
    }

    public String visit(MethodCall methodCall) {
        String object = methodCall.getObject().accept(_decoratingVisitor);
        String methodName = methodCall.getFunctionName().accept(_decoratingVisitor);

        String arguments = methodCall
                .getArguments()
                .stream()
                .map(this::visit)
                .collect(Collectors.joining(", "));

        return "%s.%s(%s)".formatted(object, methodName, arguments);
    }

    public String visit(UserType userType) {
        return userType.getName().accept(_decoratingVisitor);
    }

    public String visit(StaticImportAll staticImportAll) {
        String importTemplate = "import static %s.*;";
        return importTemplate.formatted(staticImportAll.getScope().accept(_decoratingVisitor));
    }

    public String visit(StaticImportMembers staticImportMembers) {
        StringBuilder builder = new StringBuilder();

        String importTemplate = "import static %s.%s;";
        for (Identifier member : staticImportMembers.getMembers()) {
            builder
                    .append(
                            importTemplate.formatted(
                                    staticImportMembers.getScope().accept(_decoratingVisitor),
                                    member.accept(_decoratingVisitor)
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
                            importMembers.getScope().accept(_decoratingVisitor),
                            member.accept(_decoratingVisitor)
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
            builder.append(mvcb.getMatchValue().accept(_decoratingVisitor));
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
                        .append(indent(node.accept(_decoratingVisitor)))
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
        builder.append(switchStatement.getTargetExpression().accept(_decoratingVisitor));
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
        String type = parameter.getType().accept(_decoratingVisitor);
        String name = parameter.getName().accept(_decoratingVisitor);
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
            builder.append("%s, ".formatted(parameter.accept(_decoratingVisitor)));
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

        String returnType = methodDeclaration.getReturnType().accept(_decoratingVisitor);
        builder.append(returnType).append(" ");

        String name = methodDeclaration.getName().accept(_decoratingVisitor);
        builder.append(name);

        String parameters = toStringParameters(methodDeclaration.getArguments());
        builder.append(parameters);

        return builder.toString();
    }

    public String visit(MethodDefinition methodDefinition) {
        StringBuilder builder = new StringBuilder();

        // Преобразование типа нужно, чтобы избежать вызова toString(Node node)
        String methodDeclaration = ((MethodDeclaration) methodDefinition.getDeclaration()).accept(_decoratingVisitor);
        builder.append(methodDeclaration);

        String body = methodDefinition.getBody().accept(_decoratingVisitor);
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
        builder.append(variableDeclaration.accept(_decoratingVisitor));

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

        return modifiers + "class " + decl.getName().accept(_decoratingVisitor);
    }

    public String visit(ClassDefinition def) {
        StringBuilder builder = new StringBuilder();

        String declaration = def.getDeclaration().accept(_decoratingVisitor);
        builder.append(declaration);

        String body = def.getBody().accept(_decoratingVisitor);
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
        return String.format(
                "%s %s %s",
                expr.getLeft().accept(_decoratingVisitor),
                sign,
                expr.getRight().accept(_decoratingVisitor)
        );
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
        return String.format("!%s", op.getArgument().accept(_decoratingVisitor));
    }

    public String visit(ParenthesizedExpression expr) {
        return String.format("(%s)", expr.getExpression().accept(_decoratingVisitor));
    }

    private String visit(AugmentedAssignmentOperator op, Expression left, Expression right) {
        String l = left.accept(_decoratingVisitor);
        String r = right.accept(_decoratingVisitor);

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

                String typeName = variableType.accept(_decoratingVisitor);
                String variableName = identifier.accept(_decoratingVisitor);
                addVariableToCurrentScope(identifier, variableType);
                return "%s %s = %s;".formatted(typeName, variableName, rightValue.accept(_decoratingVisitor));
            }
        }

        return "%s;".formatted(visit(assignmentOperator, leftValue, rightValue));
    }

    public String visit(Type type) {
        return switch (type) {
            case FloatType floatType -> floatType.accept(_decoratingVisitor);
            case IntType intType -> intType.accept(_decoratingVisitor);
            case BooleanType booleanType -> booleanType.accept(_decoratingVisitor);
            case StringType stringType -> stringType.accept(_decoratingVisitor);
            case NoReturn voidType -> voidType.accept(_decoratingVisitor);
            case UnknownType unknownType -> unknownType.accept(_decoratingVisitor);
            case ArrayType arrayType -> arrayType.accept(_decoratingVisitor);
            case UserType userType -> userType.accept(_decoratingVisitor);
            case CharacterType characterType -> characterType.accept(_decoratingVisitor);
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
                builder.append(dimension.accept(_decoratingVisitor));
            }

            builder.append("]");
        }

        return builder.toString();
    }

    private String visit(ArrayType type) {
        StringBuilder builder = new StringBuilder();

        String baseType = type.getItemType().accept(_decoratingVisitor);
        builder.append(baseType);
        builder.append(type.getShape().accept(_decoratingVisitor));

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

        String identifierName = identifier.accept(_decoratingVisitor);
        builder.append(identifierName);

        if (rValue != null) {
            builder.append(" = ").append(rValue.accept(_decoratingVisitor));
        }

        return builder.toString();
    }

    public String visit(VariableDeclaration stmt) {
        StringBuilder builder = new StringBuilder();

        Type declarationType = stmt.getType();
        String type = declarationType.accept(_decoratingVisitor);
        builder
                .append(type)
                .append(" ");

        for (VariableDeclarator varDecl : stmt.getDeclarators()) {
            builder.append(varDecl.accept(_decoratingVisitor)).append(", ");
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
            String s = node.accept(_decoratingVisitor);
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
        return String.format("%s;", stmt.getExpression().accept(_decoratingVisitor));
    }

    public String visit(SimpleIdentifier identifier) {
        return identifier.getName();
    }

    private String visit(ConditionBranch branch) {
        StringBuilder builder = new StringBuilder();

        String cond = branch.getCondition().accept(_decoratingVisitor);
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
                        .append(compStmt.accept(_decoratingVisitor));
            }
            else {
                builder
                        .append("\n")
                        .append(indent(compStmt.accept(_decoratingVisitor)));
            }
        }
        else {
            // В случае если тело ветки не блок кода, то добавляем отступ
            // и вставляем тело
            // Пример:
            // if (a > b)
            //     max = a;
            increaseIndentLevel();
            builder.append("\n").append(indent(body.accept(_decoratingVisitor)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String visit(BinaryComparison binComp) {
        return switch (binComp) {
            case EqOp op -> op.accept(_decoratingVisitor);
            case GeOp op -> op.accept(_decoratingVisitor);
            case GtOp op -> op.accept(_decoratingVisitor);
            case LeOp op -> op.accept(_decoratingVisitor);
            case LtOp op -> op.accept(_decoratingVisitor);
            case NotEqOp op -> op.accept(_decoratingVisitor);
            default -> throw new IllegalStateException("Unexpected value: " + binComp);
        };
    }

    public String visit(CompoundComparison cmp) {
        StringBuilder builder = new StringBuilder();

        for (BinaryComparison binComp : cmp.getComparisons()) {
            builder.append(binComp.accept(_decoratingVisitor)).append(" && ");
        }

        builder.delete(builder.length() - 4, builder.length());

        return builder.toString();
    }

    public String visit(IfStatement stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("if ");
        List<ConditionBranch> branches = stmt.getBranches();
        builder
                .append(branches.getFirst().accept(_decoratingVisitor))
                .append("\n");

        for (ConditionBranch branch : branches.subList(1, branches.size())) {
            builder
                    .append(indent("else if "))
                    .append(branch.accept(_decoratingVisitor))
                    .append("\n");
        }

        if (stmt.hasElseBranch()) {
            builder.append(indent("else"));

            Statement elseBranch = stmt.getElseBranch();
            if (elseBranch instanceof IfStatement innerIfStmt) {
                builder
                        .append(" ")
                        .append(innerIfStmt.accept(_decoratingVisitor));
            }
            else if (elseBranch instanceof CompoundStatement innerCompStmt) {
                if (_openBracketOnSameLine) {
                    builder
                            .append(" ")
                            .append(innerCompStmt.accept(_decoratingVisitor));
                }
                else {
                    builder
                            .append("\n")
                            .append(indent(innerCompStmt.accept(_decoratingVisitor)));
                }
            }
            else {
                builder
                        .append("\n")
                        .append(elseBranch.accept(_decoratingVisitor));
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
            case AssignmentExpression expr -> expr.accept(_decoratingVisitor);
            case AssignmentStatement stmt -> stmt.accept(_decoratingVisitor);
            case VariableDeclaration decl -> decl.accept(_decoratingVisitor);
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
                            .append(assignmentExpression.accept(_decoratingVisitor))
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
            String condition = generalForLoop.getCondition().accept(_decoratingVisitor);
            builder.append(condition);
        }
        builder.append("; ");

        if (generalForLoop.hasUpdate()) {
            String update = generalForLoop.getUpdate().accept(_decoratingVisitor);
            builder.append(update);
        }

        Statement body = generalForLoop.getBody();
        if (body instanceof CompoundStatement compoundStatement) {
            builder.append(")");

            if (_openBracketOnSameLine) {
                builder
                        .append(" ")
                        .append(compoundStatement.accept(_decoratingVisitor));
            }
            else {
                builder.append("\n");
                builder.append(indent(body.accept(_decoratingVisitor)));
            }
        }
        else {
            builder.append(")\n");
            increaseIndentLevel();
            builder.append(indent(body.accept(_decoratingVisitor)));
            decreaseIndentLevel();
        }

        return builder.toString();
    }

    private String getForRangeUpdate(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRange().getType() == Range.Type.UP) {
            long stepValue = forRangeLoop.getStepValueAsLong();

            if (stepValue == 1) {
                return String.format("%s++", forRangeLoop.getIdentifier().accept(_decoratingVisitor));
            }
            else {
                return String.format("%s += %d", forRangeLoop.getIdentifier().accept(_decoratingVisitor), stepValue);
            }
        }
        else if (forRangeLoop.getRange().getType() == Range.Type.DOWN) {
            long stepValue = forRangeLoop.getStepValueAsLong();

            if (stepValue == 1) {
                return String.format("%s--", forRangeLoop.getIdentifier().accept(_decoratingVisitor));
            }
            else {
                return String.format("%s -= %d", forRangeLoop.getIdentifier().accept(_decoratingVisitor), stepValue);
            }
        }

        throw new RuntimeException("Can't determine range type in for loop");
    }

    private String getForRangeHeader(RangeForLoop forRangeLoop) {
        if (forRangeLoop.getRange().getType() == Range.Type.UP) {
            String header = "int %s = %s; %s %s %s; %s";
            String compOperator = forRangeLoop.isExcludingStop() ? "<" : "<=";
            return header.formatted(
                    forRangeLoop.getIdentifier().accept(_decoratingVisitor),
                    forRangeLoop.getStart().accept(_decoratingVisitor),
                    forRangeLoop.getIdentifier().accept(_decoratingVisitor),
                    compOperator,
                    forRangeLoop.getStop().accept(_decoratingVisitor),
                    getForRangeUpdate(forRangeLoop)
            );
        }
        else if (forRangeLoop.getRange().getType() == Range.Type.DOWN) {
            String header = "int %s = %s; %s %s %s; %s";
            String compOperator = forRangeLoop.isExcludingStop() ? ">" : ">=";
            return header.formatted(
                    forRangeLoop.getIdentifier().accept(_decoratingVisitor),
                    forRangeLoop.getStart().accept(_decoratingVisitor),
                    forRangeLoop.getIdentifier().accept(_decoratingVisitor),
                    compOperator,
                    forRangeLoop.getStop().accept(_decoratingVisitor),
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
                        .append(compoundStatement.accept(_decoratingVisitor));
            }
            else {
                builder.append("\n");
                builder.append(indent(body.accept(_decoratingVisitor)));
            }
        }
        else {
            builder.append("\n");
            increaseIndentLevel();
            builder.append(indent(body.accept(_decoratingVisitor)));
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
                    indent("%s\n".formatted(node.accept(_decoratingVisitor)))
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
            builder.append("%s\n".formatted(node.accept(_decoratingVisitor)));
        }

        return builder.toString();
    }

    public String visit(ScopedIdentifier scopedIdent) {
        StringBuilder builder = new StringBuilder();

        for (var ident : scopedIdent.getScopeResolution()) {
            builder.append(ident.accept(_decoratingVisitor)).append(".");
        }
        builder.deleteCharAt(builder.length() - 1); // Удаляем последнюю точку

        return builder.toString();
    }

    public String visit(FunctionCall funcCall) {
        StringBuilder builder = new StringBuilder();

        builder.append(funcCall.getFunction().accept(_decoratingVisitor)).append("(");
        for (Expression expr : funcCall.getArguments()) {
            builder.append(expr.accept(_decoratingVisitor)).append(", ");
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
        String header = "while (" + whileLoop.getCondition().accept(_decoratingVisitor) + ")";

        Statement body = whileLoop.getBody();
        if (body instanceof CompoundStatement compStmt) {
            return header + (_openBracketOnSameLine ? " " : "\n") + compStmt.accept(_decoratingVisitor);
        }
        else {
            increaseIndentLevel();
            String result = header + "\n" + indent(body.accept(_decoratingVisitor));
            decreaseIndentLevel();
            return result;
        }
    }

    public String visit(PostfixIncrementOp inc) {
        return inc.getArgument().accept(_decoratingVisitor) + "++";
    }

    public String visit(PostfixDecrementOp dec) {
        return dec.getArgument().accept(_decoratingVisitor) + "--";
    }

    public String visit(PrefixIncrementOp inc) {
        return "++" + inc.getArgument().accept(_decoratingVisitor);
    }

    public String visit(PrefixDecrementOp dec) {
        return "--" + dec.getArgument().accept(_decoratingVisitor);
    }

    public String visit(PowOp op) {
        return "Math.pow(%s, %s)"
                .formatted(
                        op.getLeft().accept(_decoratingVisitor),
                        op.getRight().accept(_decoratingVisitor)
                );
    }

    public String visit(PackageDeclaration decl) {
        return "package %s;".formatted(decl.getPackageName().accept(_decoratingVisitor));
    }
}

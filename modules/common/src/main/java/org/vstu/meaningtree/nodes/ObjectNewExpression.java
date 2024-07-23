package org.vstu.meaningtree.nodes;

import org.vstu.meaningtree.nodes.declarations.DeclarationArgument;
import org.vstu.meaningtree.nodes.definitions.DefinitionArgument;
import org.vstu.meaningtree.nodes.identifiers.SimpleIdentifier;

import java.util.List;
import java.util.Objects;

public class ObjectNewExpression extends NewExpression {
    private final List<Expression> _constructorArguments;

    public ObjectNewExpression(Type type, Expression... constructorArguments) {
        this(type, List.of(constructorArguments));
    }

    public ObjectNewExpression(Type type, List<Expression> constructorArguments) {
        super(type);
        _constructorArguments = List.copyOf(constructorArguments);
    }

    public List<Expression> getConstructorArguments() {
        return _constructorArguments;
    }

    // anonymous classes unsupported

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ObjectNewExpression that = (ObjectNewExpression) o;
        return Objects.equals(_constructorArguments, that._constructorArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _constructorArguments);
    }
}

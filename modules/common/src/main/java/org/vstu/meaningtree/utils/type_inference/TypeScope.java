package org.vstu.meaningtree.utils.type_inference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.types.UnknownType;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code TypeScope} представляет область видимости, которая хранит типы сущностей:
 * <ul>
 *     <li>переменных и</li>
 *     <li>методов с функциями (ограничение: хранится только возвращаемый тип значения).</li>
 * </ul>
 * Поддерживает вложенные области видимости через ссылку на родительскую область.
 */
public class TypeScope {
    /**
     * Родительская область видимости. Может быть {@code null} для корневой области.
     */
    @Nullable
    private final TypeScope _parentScope;

    /**
     * Карта имён переменных на их типы.
     */
    @NotNull
    private final Map<SimpleIdentifier, Type> _variables;

    /**
     * Карта имён методов на их возвращаемые типы.
     */
    @NotNull
    private final Map<SimpleIdentifier, Type> _methods;

    /**
     * Создаёт область видимости с указанием родительской.
     *
     * @param parentScope родительская область видимости или {@code null}, если это корневая область
     */
    public TypeScope(@Nullable TypeScope parentScope) {
        _parentScope = parentScope;
        _variables = new HashMap<>();
        _methods = new HashMap<>();
    }

    /**
     * Создаёт корневую область видимости (без родителя).
     */
    public TypeScope() {
        this(null);
    }

    /**
     * Возвращает родительскую область видимости.
     *
     * @return родительская область или {@code null}, если область корневая
     */
    @Nullable
    public TypeScope getParentScope() {
        return _parentScope;
    }

    /**
     * Добавляет новую переменную в текущую область видимости.
     *
     * @param variableName идентификатор переменной, не может быть {@code null}
     * @param type         тип переменной, не может быть {@code null}
     */
    public void addVariable(@NotNull SimpleIdentifier variableName, @NotNull Type type) {
        _variables.put(variableName, type);
    }

    /**
     * Добавляет новый метод в текущую область видимости.
     * Хранится только возвращаемый тип метода.
     *
     * @param methodName имя метода, не может быть {@code null}
     * @param returnType возвращаемый тип метода, не может быть {@code null}
     */
    public void addMethod(@NotNull SimpleIdentifier methodName, @NotNull Type returnType) {
        _methods.put(methodName, returnType);
    }

    /**
     * Получает тип переменной по её идентификатору.
     * Если переменная не найдена в текущей области, поиск продолжается в родительской области.
     *
     * @param variableName идентификатор переменной, не может быть {@code null}
     * @return тип переменной или {@code null}, если переменная не объявлена ни в этой, ни в родительских областях
     */
    @Nullable
    public Type getVariableType(@NotNull SimpleIdentifier variableName) {
        Type variableType = _variables.getOrDefault(variableName, null);

        if (variableType == null) {
            if (_parentScope == null) {
                return null;
            }
            variableType = _parentScope.getVariableType(variableName);
        }

        return variableType;
    }

    /**
     * Изменяет тип существующей переменной или создаёт новую, если таковой не было.
     *
     * @param variableName     идентификатор переменной, не может быть {@code null}
     * @param type             новый тип переменной, не может быть {@code null}
     * @param createIfNotExists если {@code true}, создаёт переменную, если она отсутствует;
     *                          если {@code false}, бросает {@link IllegalArgumentException}
     *                          при попытке изменить несуществующую переменную
     * @throws IllegalArgumentException если переменная не найдена и
     *                                  {@code createIfNotExists} равно {@code false}
     */
    public void changeVariableType(
            @NotNull SimpleIdentifier variableName,
            @NotNull Type type,
            boolean createIfNotExists
    ) {
        if (getVariableType(variableName) == null && !createIfNotExists) {
            throw new IllegalArgumentException("No such variable found: %s".formatted(variableName));
        }
        _variables.put(variableName, type);
    }

    /**
     * Изменяет тип существующей переменной или создаёт новую.
     *
     * @param variableName идентификатор переменной, не может быть {@code null}
     * @param type         новый тип переменной, не может быть {@code null}
     */
    public void changeVariableType(
            @NotNull SimpleIdentifier variableName,
            @NotNull Type type
    ) {
        changeVariableType(variableName, type, true);
    }

    /**
     * Получает возвращаемый тип метода по его идентификатору.
     * Если метод не найден в текущей области, поиск продолжается в родительской.
     *
     * @param methodName идентификатор метода, не может быть {@code null}
     * @return возвращаемый тип метода или {@link UnknownType},
     * если метод не объявлен ни в этой, ни в родительских областях
     */
    @NotNull
    public Type getMethodReturnType(@NotNull SimpleIdentifier methodName) {
        Type methodType = _methods.getOrDefault(methodName, null);

        if (methodType == null) {
            if (_parentScope == null) {
                return new UnknownType();
            }
            methodType = _parentScope.getMethodReturnType(methodName);
        }

        return methodType;
    }

    /**
     * Возвращает карту всех переменных текущей области видимости вместе с их типами.
     *
     * @return неизменяемый (модифицируемый) {@link Map} из идентификаторов переменных в их типы
     */
    @NotNull
    public Map<SimpleIdentifier, Type> getVariables() {
        return _variables;
    }

    /**
     * Возвращает строковое представление текущей области видимости,
     * перечисляя все переменные и их типы в формате {@code "name = type"}.
     *
     * @return строка с перечислением переменных и их типов
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (var entry : _variables.entrySet()) {
            builder.append(
                    "%s = %s, ".formatted(entry.getKey(), entry.getValue())
            );
        }

        if (builder.length() > 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }
}
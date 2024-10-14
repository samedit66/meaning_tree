package org.vstu.meaningtree.utils;

public enum TokenType {
    OPERATOR,

    CONST,
    CALLABLE_IDENTIFIER,
    IDENTIFIER,
    KEYWORD,

    OPEN_BRACE,
    CLOSE_BRACE,
    SUBSCRIPT_OPEN_BRACE,
    SUBSCRIPT_CLOSE_BRACE,
    CALL_OPEN_BRACE,
    CALL_CLOSE_BRACE,
    COMPOUND_OPEN_BRACE,
    COMPOUND_CLOSE_BRACE,


    STATEMENT_TOKEN,
    SEPARATOR,
    COMMA,
    UNKNOWN
}

package org.vstu.meaningtree.nodes.modules;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.literals.StringLiteral;

public class Include extends Node {
    private StringLiteral filename;

    public enum IncludeType {
        // #include "defs.h"
        QUOTED_FORM,
        // #include <stdio.h>
        POINTY_BRACKETS_FORM,
    }

    private final IncludeType _includeType;

    public Include(StringLiteral filename, IncludeType includeType) {
        _includeType = includeType;
    }

    public StringLiteral getFileName() {
        return filename;
    }

    public IncludeType getIncludeType() {
        return _includeType;
    }
}

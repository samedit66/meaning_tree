package org.vstu.meaningtree.nodes.statements;

import org.vstu.meaningtree.nodes.CanInitialize;
import org.vstu.meaningtree.nodes.Node;

import java.util.List;

public class CompoundAssignmentStatement extends Node implements CanInitialize {
    /**
     * This node used only in case of compound assignment in for header
     * Example: for(a = 1, b = 3; i < 3; i++)
     */

    private final List<AssignmentStatement> _assignments;

    public CompoundAssignmentStatement(AssignmentStatement ... assignments) {
        this._assignments = List.of(assignments);
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    public AssignmentStatement[] getAssignments() {
        return _assignments.toArray(new AssignmentStatement[0]);
    }

}

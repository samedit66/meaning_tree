package org.vstu.meaningtree.nodes.expressions.identifiers;

import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.other.MemberAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScopedIdentifier extends Identifier {
    private List<SimpleIdentifier> _scopeResolutionList;

    public ScopedIdentifier(SimpleIdentifier... identifiers) {
        _scopeResolutionList = List.of(identifiers);
    }

    public ScopedIdentifier(List<SimpleIdentifier> identifiers) {
        _scopeResolutionList = List.copyOf(identifiers);
    }

    public List<SimpleIdentifier> getScopeResolution() {
        return _scopeResolutionList;
    }

    @Override
    public String generateDot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScopedIdentifier that = (ScopedIdentifier) o;
        return Objects.equals(_scopeResolutionList, that._scopeResolutionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _scopeResolutionList);
    }

    @Override
    public ScopedIdentifier clone() {
        ScopedIdentifier obj = (ScopedIdentifier) super.clone();
        obj._scopeResolutionList = new ArrayList<>(_scopeResolutionList.stream().map(SimpleIdentifier::clone).toList());
        return obj;
    }

    @Override
    public boolean contains(Identifier other) {
        return _scopeResolutionList.contains(other);
    }

    @Override
    public int contentSize() {
        return _scopeResolutionList.size();
    }

    public MemberAccess toMemberAccess() {
        if (_scopeResolutionList.size() == 2) {
            return new MemberAccess(_scopeResolutionList.getFirst(), _scopeResolutionList.getLast());
        }
        return new MemberAccess(
                new ScopedIdentifier(_scopeResolutionList.subList(0, _scopeResolutionList.size() - 1))
                        .toMemberAccess(), _scopeResolutionList.getLast());
    }
}

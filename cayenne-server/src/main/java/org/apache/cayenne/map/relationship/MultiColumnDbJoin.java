package org.apache.cayenne.map.relationship;

public class MultiColumnDbJoin extends DbJoin {

    private ColumnPair[] pairs;

    public MultiColumnDbJoin(ColumnPair[] pairs) {
        this.pairs = pairs;
    }

    @Override
    public <T> T accept(JoinVisitor<T> joinVisitor) {
        return joinVisitor.visit(pairs);
    }
}

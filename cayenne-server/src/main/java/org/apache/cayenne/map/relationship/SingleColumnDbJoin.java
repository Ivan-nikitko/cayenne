package org.apache.cayenne.map.relationship;

public class SingleColumnDbJoin extends DbJoin {

    private ColumnPair columnPair;

    public SingleColumnDbJoin(ColumnPair columnPair) {
        this.columnPair = columnPair;
    }

    @Override
    public <T> T accept(JoinVisitor<T> joinVisitor) {
        return joinVisitor.visit(columnPair);
    }
}

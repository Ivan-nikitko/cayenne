package org.apache.cayenne.map.relationship;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.map.DataMap;

public class DbRelationshipBuilder {

    protected DbJoin dbJoin;
    protected String[] dbEntities;
    protected String[] names;
    protected ToDependentPkSemantics toDependentPkSemantics;
    protected ToManySemantics toManySemantics;

    protected DataMap dataMap;

    public DbRelationshipBuilder() {}

    public DbRelationshipBuilder join(DbJoin dbJoin) {
        this.dbJoin = dbJoin;
        return this;
    }

    public DbRelationshipBuilder entities(String[] dbEntities) {
        if(dbEntities.length != 2) {
            throw new CayenneRuntimeException("Invalid number of relationship's entities");
        }
        this.dbEntities = dbEntities;
        return this;
    }

    public DbRelationshipBuilder names(String[] names) {
        if(names.length != 2) {
            throw new CayenneRuntimeException("Invalid number of relationship's names");
        }
        this.names = names;
        return this;
    }

    public DbRelationshipBuilder toDepPkSemantics(ToDependentPkSemantics toDependentPkSemantics) {
        this.toDependentPkSemantics = toDependentPkSemantics;
        return this;
    }

    public DbRelationshipBuilder toManySemantics(ToManySemantics toManySemantics) {
        this.toManySemantics = toManySemantics;
        return this;
    }

    public DbRelationshipBuilder dataMap(DataMap dataMap) {
        this.dataMap = dataMap;
        return this;
    }

    public String[] getDbEntities(){
        return dbEntities;
    }

    public String[] getNames() {
        return names;
    }

    public ToManySemantics getToManySemantics() {
        return toManySemantics;
    }

    public ToDependentPkSemantics getToDependentPkSemantics() {
        return toDependentPkSemantics;
    }

    public DbJoin getDbJoinCondition() {
        return dbJoin;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public DbRelationship build() {
        if(dbJoin == null ||
                dbEntities == null ||
                names == null ||
                toDependentPkSemantics == null ||
                toManySemantics == null ||
                dataMap == null) {
            throw new CayenneRuntimeException("Miss parameters to create dbJoin.");
        }
        return new DbRelationship(
                dbJoin,
                dbEntities,
                names,
                toDependentPkSemantics,
                toManySemantics,
                dataMap);
    }

}

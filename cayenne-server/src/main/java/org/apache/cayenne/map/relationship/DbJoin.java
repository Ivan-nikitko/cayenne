package org.apache.cayenne.map.relationship;

import java.io.Serializable;

public abstract class DbJoin implements Serializable {

    public abstract<T> T accept(JoinVisitor<T> joinVisitor);

}

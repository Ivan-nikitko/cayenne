/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.map.relationship;

import org.apache.cayenne.access.translator.select.TranslatorContext;
import org.apache.cayenne.dba.QuotingStrategy;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.JoinType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class DbJoinCodeExamples {


    public static final char OUTER_JOIN_INDICATOR = '+';
    public static final char SPLIT_PATH_INDICATOR = '#';

    private org.apache.cayenne.map.relationship.DbRelationship relationship;

    protected List<String> attributePaths;
    protected List<DbAttribute> attributes;
    protected StringBuilder currentDbPath;
    protected String currentAlias;

    protected QuotingStrategy quotingStrategy;

    private boolean isOuterJoin;

    protected TranslatorContext context;

    private DataMap dataMap;
    private DbEntity entity1;
    private DbEntity entity2;
    private DbJoinCondition dbJoinCondition;


    @Before
    public void setUp() throws Exception {


        isOuterJoin = true;

        dataMap = new DataMap();

        entity1 = new DbEntity("entity1");
        DbAttribute attr1 = new DbAttribute("attr1");
        attr1.setEntity(entity1);
        entity1.addAttribute(attr1);

        dataMap.addDbEntity(entity1);

        entity2 = new DbEntity("entity2");
        DbAttribute attr2 = new DbAttribute("attr2");
        attr2.setEntity(entity2);
        attr2.setPrimaryKey(true);
        attr2.setMandatory(true);

        dataMap.addDbEntity(entity2);

        dbJoinCondition = new SinglePairCondition(new ColumnPair("attr1", "attr2"));

        this.attributes = new ArrayList<>(1);
        this.attributePaths = new ArrayList<>(1);
        this.currentDbPath = new StringBuilder("path");

        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .dataMap(dataMap)
                .build();
        relationship = dbJoin.getRelationhsip();


    }


    /**
     * DbpathProcessor
     */
    @Test
    public void processRelTermination() {

        String path = currentDbPath.toString();
        appendCurrentPath(relationship.getName());

        if (relationship.isToMany() || !relationship.isToPK()) {
            // match on target PK
            context.getTableTree().addJoinTable(currentDbPath.toString(), relationship, isOuterJoin()
                    ? JoinType.LEFT_OUTER
                    : JoinType.INNER);
            path = currentDbPath.toString();
            for (DbAttribute attribute : relationship.getTargetEntity().getPrimaryKeys()) {
                addAttribute(path, attribute);
            }
        } else {
            String finalPath = path;
            relationship.accept(new DirectionalJoinVisitor<Void>() {
                @Override
                public Void visit(DbAttribute[] source, DbAttribute[] target) {
                    for (DbAttribute attribute : source) {
                        addAttribute(finalPath, attribute);
                    }
                    return null;
                }

                @Override
                public Void visit(DbAttribute source, DbAttribute target) {
                    addAttribute(finalPath, source);
                    return null;
                }
            });
        }

    }


    /**
     * ProjectUtil
     *
     * @param relationship
     */
    @Test
    public  boolean containsSourceAttribute(
            org.apache.cayenne.map.relationship.DbRelationship relationship,
            DbAttribute attribute) {
        if (attribute.getEntity() != relationship.getSourceEntity()) {
            return false;
        }

        return relationship.accept(new DirectionalJoinVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] source, DbAttribute[] target) {
                for (DbAttribute src : source) {
                    if (src == attribute) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Boolean visit(DbAttribute source, DbAttribute target) {
                return source == attribute;
            }
        });
    }



    /**
     * JdbcAdapter
     * Returns a SQL string that can be used to create a foreign key constraint
     * for the relationship.
     */
    public String createFkConstraint(DbRelationship rel) {

        DbEntity source = rel.getSourceEntity();
        StringBuilder buf = new StringBuilder();
        StringBuilder refBuf = new StringBuilder();

        buf.append("ALTER TABLE ");

        buf.append(quotingStrategy.quotedFullyQualifiedName(source));
        buf.append(" ADD FOREIGN KEY (");

        if(rel.isToPK()) {
            List<DbAttribute> pks = rel.getTargetEntity().getPrimaryKeys();
            rel.accept(new SortDirectionalJoinVisitor(pks));
        }

        final AtomicBoolean first = new AtomicBoolean(true);
        rel.accept(new DirectionalJoinVisitor<Void>() {

            private void buildString(DbAttribute source, DbAttribute target) {
                if (first.get()) {
                    first.set(false);
                } else {
                    buf.append(", ");
                    refBuf.append(", ");
                }

                buf.append(quotingStrategy.quotedIdentifier(source.getEntity().getDataMap(), source.getName()));
                refBuf.append(quotingStrategy.quotedIdentifier(target.getEntity().getDataMap(), target.getName()));
            }

            @Override
            public Void visit(DbAttribute[] source, DbAttribute[] target) {
                int length = source.length;
                for(int i = 0; i < length; i++) {
                    buildString(source[i], target[i]);
                }
                return null;
            }

            @Override
            public Void visit(DbAttribute source, DbAttribute target) {
                buildString(source, target);
                return null;
            }
        });

        buf.append(") REFERENCES ");

        buf.append(quotingStrategy.quotedFullyQualifiedName(rel.getTargetEntity()));

        buf.append(" (").append(refBuf.toString()).append(')');
        return buf.toString();
    }



    protected void addAttribute(String path, DbAttribute attribute) {
        attributePaths.add(path);
        attributes.add(attribute);
    }

    protected void appendCurrentPath(String nextSegment) {
        if (currentDbPath.length() > 0) {
            currentDbPath.append('.');
        }
        currentDbPath.append(nextSegment);
        if (currentAlias != null) {
            currentDbPath.append(SPLIT_PATH_INDICATOR).append(currentAlias);
        }
        if (isOuterJoin) {
            currentDbPath.append(OUTER_JOIN_INDICATOR);
        }
    }

    public boolean isOuterJoin() {
        return isOuterJoin;
    }

    private static class SortDirectionalJoinVisitor implements DirectionalJoinVisitor<Void> {
        private final List<DbAttribute> pks;

        public SortDirectionalJoinVisitor(List<DbAttribute> pks) {
            this.pks = pks;
        }

        @Override
        public Void visit(DbAttribute[] source, DbAttribute[] target) {
            List<DbAttribute> sourceList = Arrays.asList(source);
            Arrays.sort(source,
                    Comparator
                            .comparingInt(sourceAttr ->
                                    pks.indexOf(target[sourceList.indexOf(sourceAttr)])));
            Arrays.sort(target, Comparator.comparingInt(pks::indexOf));
            return null;
        }

        @Override
        public Void visit(DbAttribute source, DbAttribute target) {
            return null;
        }
    }

}

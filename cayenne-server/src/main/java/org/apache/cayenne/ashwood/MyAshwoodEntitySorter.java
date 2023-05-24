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

package org.apache.cayenne.ashwood;

import org.apache.cayenne.ashwood.graph.Digraph;
import org.apache.cayenne.ashwood.graph.IndegreeTopologicalSort;
import org.apache.cayenne.ashwood.graph.MapDigraph;
import org.apache.cayenne.ashwood.graph.StrongConnection;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.DbRelationshipBuilder;
import org.apache.cayenne.map.relationship.DbRelationshipSide;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;
import org.apache.cayenne.map.relationship.MultiColumnDbJoin;
import org.apache.cayenne.map.relationship.SingleColumnDbJoin;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implements dependency sorting algorithms for ObjEntities, DbEntities and
 * DataObjects. Presently it works for acyclic database schemas with possible
 * multi-reflexive tables.
 *
 * @since 3.1
 */
public class MyAshwoodEntitySorter extends AshwoodEntitySorter {

    protected Map<DbEntity, List<DbRelationshipSide>> reflexiveDbEntities;


    @Override
    /**
     * Reindexes internal sorter without synchronization.
     */
    protected void doIndexSorter() {

        Map<DbEntity, List<DbRelationshipSide>> reflexiveDbEntities = new HashMap<>();
        Digraph<DbEntity, List<DbAttribute>> referentialDigraph = new MapDigraph<>();

        if (entityResolver != null) {
            for (DbEntity entity : entityResolver.getDbEntities()) {
                referentialDigraph.addVertex(entity);
            }
        }

        Map<DbEntity, List<DbRelationshipSide>> dbEntityRelationshipSidesMap = getEntityRelationshipSideMap();

        for (DbEntity destination : entityResolver.getDbEntities()) {
                for (DbRelationshipSide candidate : dbEntityRelationshipSidesMap.get(destination)) {
                    if ((!candidate.isToMany() && !candidate.isToDependentPK()) || candidate.isToMasterPK()) {
                        DbEntity origin = candidate.getTargetEntity();
                        final AtomicBoolean newReflexive = new AtomicBoolean(destination.equals(origin));
                        candidate.accept(new DirectionalJoinVisitor<Void>() {

                            private void build(DbAttribute target) {
                                if (target.isPrimaryKey()) {

                                    if (newReflexive.get()) {
                                        List<DbRelationshipSide> reflexiveRels = reflexiveDbEntities
                                                .computeIfAbsent(destination, k ->
                                                        new ArrayList<>(1));
                                        reflexiveRels.add(candidate);
                                        newReflexive.set(false);
                                    }

                                    List<DbAttribute> fks = referentialDigraph.getArc(origin, destination);
                                    if (fks == null) {
                                        fks = new ArrayList<>();
                                        referentialDigraph.putArc(origin, destination, fks);
                                    }

                                    fks.add(target);
                                }
                            }

                            @Override
                            public Void visit(DbAttribute[] source, DbAttribute[] target) {
                                int length = source.length;
                                for (int i = 0; i < length; i++) {
                                    build(target[i]);
                                }
                                return null;
                            }

                            @Override
                            public Void visit(DbAttribute source, DbAttribute target) {
                                build(target);
                                return null;
                            }
                        });
                    }
            }
        }

        StrongConnection<DbEntity, List<DbAttribute>> contractor = new StrongConnection<>(referentialDigraph);

        Digraph<Collection<DbEntity>, Collection<List<DbAttribute>>> contractedReferentialDigraph = new MapDigraph<>();
        contractor.contract(contractedReferentialDigraph);

        IndegreeTopologicalSort<Collection<DbEntity>> sorter = new IndegreeTopologicalSort<>(
                contractedReferentialDigraph);

        Map<DbEntity, MyAshwoodEntitySorter.ComponentRecord> components = new HashMap<>(contractedReferentialDigraph.order());
        int componentIndex = 0;
        while (sorter.hasNext()) {
            Collection<DbEntity> component = sorter.next();
            MyAshwoodEntitySorter.ComponentRecord rec = new MyAshwoodEntitySorter.ComponentRecord(componentIndex++, component);

            for (DbEntity table : component) {
                components.put(table, rec);
            }
        }

        this.reflexiveDbEntities = reflexiveDbEntities;
        this.components = components;
    }

    private Map<DbEntity, List<DbRelationshipSide>> getEntityRelationshipSideMap() {
        Map<DbEntity, List<DbRelationshipSide>> dbEntityRelationshipSidesMap = new HashMap<>();
        for (DbEntity entity : entityResolver.getDbEntities()) {
            dbEntityRelationshipSidesMap.put(entity, convertEntityDbRelationships(entity));
        }
        return dbEntityRelationshipSidesMap;
    }


    private List<DbRelationshipSide> convertEntityDbRelationships(DbEntity entity) {
        Collection<org.apache.cayenne.map.DbRelationship> relationships = entity.getRelationships();
        List<DbRelationshipSide> dbRelationshipSides = new ArrayList<>(relationships.size());

        for (org.apache.cayenne.map.DbRelationship relationship : relationships) {

            DataMap dataMap = relationship.getSourceEntity().getDataMap();

            List<DbJoin> joins = relationship.getJoins();

            ColumnPair[] columnPairs = joins.stream()
                    .map(join -> new ColumnPair(join.getSourceName(), join.getTargetName()))
                    .toArray(ColumnPair[]::new);

            org.apache.cayenne.map.relationship.DbJoin dbJoin = (columnPairs.length == 1)
                    ? new SingleColumnDbJoin(columnPairs[0])
                    : new MultiColumnDbJoin(columnPairs);

            ToDependentPkSemantics toDepPkSemantics = ToDependentPkSemantics.getSemantics(relationship.isToDependentPK(),
                    relationship.getReverseRelationship().isToDependentPK());
            ToManySemantics toManySemantics = ToManySemantics.getSemantics(relationship.isToMany(),
                    relationship.getReverseRelationship().isToMany());

            DbRelationship dbRelationship = new DbRelationshipBuilder()
                    .join(dbJoin)
                    .entities(new String[]{relationship.getSourceEntityName(), relationship.getTargetEntityName()})
                    .names(new String[]{relationship.getReverseRelationship().getName(), relationship.getName()})
                    .toDepPkSemantics(toDepPkSemantics)
                    .toManySemantics(toManySemantics)
                    .dataMap(dataMap)
                    .build();

            dbRelationship.compile(dataMap);

            dbRelationshipSides.add(dbRelationship.getRelationshipSide());
            dbRelationshipSides.add(dbRelationship.getRelationshipSide().getReverseRelationshipSide());
        }
        return dbRelationshipSides;
    }



}

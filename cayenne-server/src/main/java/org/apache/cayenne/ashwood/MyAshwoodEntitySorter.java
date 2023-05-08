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

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.access.flush.operation.DbRowOp;
import org.apache.cayenne.ashwood.graph.Digraph;
import org.apache.cayenne.ashwood.graph.IndegreeTopologicalSort;
import org.apache.cayenne.ashwood.graph.MapDigraph;
import org.apache.cayenne.ashwood.graph.StrongConnection;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.EntitySorter;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.reflect.ClassDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Implements dependency sorting algorithms for ObjEntities, DbEntities and
 * DataObjects. Presently it works for acyclic database schemas with possible
 * multi-reflexive tables.
 * 
 * @since 3.1
 */
public class MyAshwoodEntitySorter extends AshwoodEntitySorter {

	protected EntityResolver entityResolver;
	protected Map<DbEntity, ComponentRecord> components;
	protected Map<DbEntity, List<DbRelationship>> reflexiveDbEntities;




	@Override
	protected void doIndexSorter() {

		Map<DbEntity, List<org.apache.cayenne.map.relationship.DbRelationship>> reflexiveDbEntities = new HashMap<>();
		Digraph<DbEntity, List<DbAttribute>> referentialDigraph = new MapDigraph<>();

		if (entityResolver != null) {
			for (DbEntity entity : entityResolver.getDbEntities()) {
				referentialDigraph.addVertex(entity);
			}
		}

		for (DbEntity destination : entityResolver.getDbEntities()) {
			for (org.apache.cayenne.map.relationship.DbRelationship candidate : destination.getRelationships()) {
				if ((!candidate.isToMany() && !candidate.isToDependentPK()) || candidate.isToMasterPK()) {
					DbEntity origin = candidate.getTargetEntity();
					final AtomicBoolean newReflexive = new AtomicBoolean(destination.equals(origin));
					candidate.accept(new DirectionalJoinVisitor<Void>() {

						private void build(DbAttribute target) {
							if (target.isPrimaryKey()) {

								if (newReflexive.get()) {
									List<org.apache.cayenne.map.relationship.DbRelationship> reflexiveRels = reflexiveDbEntities
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
							for(int i = 0; i < length; i++) {
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

	private final static class ComponentRecord {

		ComponentRecord(int index, Collection<DbEntity> component) {
			this.index = index;
			this.component = component;
		}

		int index;
		Collection<DbEntity> component;
	}


}

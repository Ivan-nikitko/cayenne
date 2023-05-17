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

import org.apache.cayenne.di.Inject;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.DbRelationshipBuilder;
import org.apache.cayenne.map.relationship.DbRelationshipSide;
import org.apache.cayenne.map.relationship.MultiColumnDbJoin;
import org.apache.cayenne.map.relationship.SingleColumnDbJoin;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.apache.cayenne.unit.di.server.CayenneProjects;
import org.apache.cayenne.unit.di.server.ServerCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

@UseServerRuntime(CayenneProjects.TESTMAP_PROJECT)
public class MyAshwoodEntitySorterIT extends ServerCase {


    @Inject
    private EntityResolver resolver;
    private MyAshwoodEntitySorter sorter;

    private DbEntity artist;
    private DbEntity artistExhibit;
    private DbEntity exhibit;
    private DbEntity gallery;
    private DbEntity painting;
    private DbEntity paintingInfo;

    private List<DbEntity> entities;

    private DbRelationshipSide[] relationshipSides;


    @Before
    public void before() {


        this.sorter = new MyAshwoodEntitySorter();
        //  prepareRelationships();

        sorter.setEntityResolver(resolver);


        this.artist = resolver.getDbEntity("ARTIST");
        this.artistExhibit = resolver.getDbEntity("ARTIST_EXHIBIT");
        this.exhibit = resolver.getDbEntity("EXHIBIT");
        this.gallery = resolver.getDbEntity("GALLERY");
        this.painting = resolver.getDbEntity("PAINTING");
        this.paintingInfo = resolver.getDbEntity("PAINTING_INFO");

        this.entities = Arrays.asList(artist, artistExhibit, exhibit, gallery, painting, paintingInfo);
        List<DbRelationshipSide> dbRelationshipSides = new ArrayList<>();
        for (DbEntity entity : entities) {
            dbRelationshipSides.addAll(convertDbRelationships(entity));
        }
        sorter.setRelationshipSides(dbRelationshipSides);
    }

    @Test
    public void testSortDbEntities() {

        Collections.shuffle(entities);

        sorter.sortDbEntities(entities, false);

        assertTrue(entities.indexOf(artist) < entities.indexOf(artistExhibit));
        assertTrue(entities.indexOf(artist) < entities.indexOf(painting));
        assertTrue(entities.indexOf(gallery) < entities.indexOf(exhibit));
        assertTrue(entities.indexOf(exhibit) < entities.indexOf(artistExhibit));
        assertTrue(entities.indexOf(painting) < entities.indexOf(paintingInfo));
    }

    private List<DbRelationshipSide> convertDbRelationships(DbEntity entity) {
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
            //TODO toDepPk toMany


            DbRelationship dbRelationship = new DbRelationshipBuilder()
                    .join(dbJoin)
                    .entities(new String[]{relationship.getSourceEntityName(), relationship.getTargetEntityName()})
                    .names(new String[]{relationship.getReverseRelationship().getName(), relationship.getName()})
                    .toDepPkSemantics(relationship.isToDependentPK() ? ToDependentPkSemantics.LEFT : ToDependentPkSemantics.NONE)
                    .toManySemantics(relationship.isToMany() ? ToManySemantics.ONE_TO_MANY : ToManySemantics.ONE_TO_ONE)
                    .dataMap(dataMap)
                    .build();

            dbRelationship.compile(dataMap);

            dbRelationshipSides.add(dbRelationship.getRelationshipSide());
            dbRelationshipSides.add(dbRelationship.getRelationshipSide().getReverseRelationshipSide());
        }
        return dbRelationshipSides;
    }

}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Before
    public void before() {


        this.sorter = new MyAshwoodEntitySorter();

        sorter.setEntityResolver(resolver);

        this.artist = resolver.getDbEntity("ARTIST");
        this.artistExhibit = resolver.getDbEntity("ARTIST_EXHIBIT");
        this.exhibit = resolver.getDbEntity("EXHIBIT");
        this.gallery = resolver.getDbEntity("GALLERY");
        this.painting = resolver.getDbEntity("PAINTING");
        this.paintingInfo = resolver.getDbEntity("PAINTING_INFO");

        this.entities = Arrays.asList(artist, artistExhibit, exhibit, gallery, painting, paintingInfo);
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

}

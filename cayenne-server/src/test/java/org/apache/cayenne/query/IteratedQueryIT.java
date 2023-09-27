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
package org.apache.cayenne.query;

import org.apache.cayenne.ResultBatchIterator;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.test.jdbc.DBHelper;
import org.apache.cayenne.test.jdbc.TableHelper;
import org.apache.cayenne.testdo.testmap.Artist;
import org.apache.cayenne.testdo.testmap.Painting;
import org.apache.cayenne.unit.di.server.CayenneProjects;
import org.apache.cayenne.unit.di.server.ServerCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@UseServerRuntime(CayenneProjects.TESTMAP_PROJECT)
public class IteratedQueryIT extends ServerCase {

	@Inject
	private DataContext context;

	@Inject
	private DBHelper dbHelper;

	private TableHelper tPainting;

	private TableHelper tArtist;


	@Before
	public void before() {
		tPainting = new TableHelper(dbHelper, "PAINTING")
				.setColumns("PAINTING_ID", "PAINTING_TITLE", "ESTIMATED_PRICE", "ARTIST_ID")
				.setColumnTypes(Types.INTEGER, Types.VARCHAR, Types.DECIMAL, Types.INTEGER);

		tArtist = new TableHelper(dbHelper, "ARTIST")
				.setColumns("ARTIST_ID", "ARTIST_NAME");

	}

	private void createPaintingsDataSet() throws Exception {
		for (int i = 1; i <= 20; i++) {
			tPainting.insert(i, "painting" + i, 10000. * i, 1);
		}
	}

	private void createArtistDataSet() throws SQLException {
		tArtist.insert(1, "Test1");
		tArtist.insert(2, "Test2");
	}

	@Test
	public void test_PrefetchSelectFirstWithoutIterator() throws Exception {
		createArtistDataSet();
		createPaintingsDataSet();

		Painting painting = ObjectSelect.query(Painting.class).prefetch(Painting.TO_ARTIST.joint()).selectFirst(context);

		Artist artist = (Artist) painting.readPropertyDirectly("toArtist");
		assertNotNull(artist);
		assertEquals("Test1", artist.readPropertyDirectly("artistName"));
	}

	@Test
	public void test_prefetchBatchIterator() throws Exception {
		createArtistDataSet();
		createPaintingsDataSet();

		ResultBatchIterator<Painting> iterator = ObjectSelect.query(Painting.class)
				.prefetch(Painting.TO_ARTIST.joint())
				.batchIterator(context, 2);
		Painting painting = iterator.next().get(1);

		assertTrue(painting instanceof Painting);
		assertEquals("painting2",painting.readPropertyDirectly("paintingTitle"));

		Artist artist = (Artist) painting.readPropertyDirectly("toArtist");
		assertTrue(artist instanceof Artist);
		assertNotNull(artist);
		assertEquals("Test1", artist.readPropertyDirectly("artistName"));
	}


	//CAY-2812
	@Test
	public void test_BatchIteratorNoMap() throws Exception {
		createArtistDataSet();
		createPaintingsDataSet();
		//TODO check iterator

		ResultBatchIterator<Painting> iterator = ObjectSelect.query(Painting.class)
				.prefetch(Painting.TO_ARTIST.joint())
				.batchIterator(context, 5);
		Painting painting = iterator.next().get(0);
		assertTrue(painting instanceof Painting);

	}

	//CAY-2812
	@Test
	public void test_BatchIteratorAfterMap() throws Exception {
		createArtistDataSet();
		createPaintingsDataSet();
		ResultBatchIterator<DTO> batchIterator = ObjectSelect.columnQuery(Painting.class, Painting.PAINTING_TITLE, Painting.ESTIMATED_PRICE)
				.map(this::toDto)
				.batchIterator(context, 2);

		assertTrue(batchIterator.iterator().next().get(0) instanceof DTO);

	}

	@Test
	public void testSelectFirstAfterMap() throws Exception {
		createArtistDataSet();
		createPaintingsDataSet();

		DTO dto = ObjectSelect.columnQuery(Painting.class, Painting.PAINTING_TITLE, Painting.ESTIMATED_PRICE)
				.map(this::toDto)
				.selectFirst(context);

		assertTrue(dto instanceof DTO);

		System.out.println(dto.toString());
	}

	DTO toDto(Object[] data) {
		return new DTO(data);
	}


	static class DTO {
		private final String title;
		private final Long estimatedPrice;

		public DTO(Object [] data) {
			this.title = (String) data[0];
			this.estimatedPrice = ((Number)data[1]).longValue();;

		}

		@Override
		public String toString() {
			return "DTO{" +
					"title='" + title + '\'' +
					", estimatedPrice=" + estimatedPrice +
					'}';
		}
	}
}

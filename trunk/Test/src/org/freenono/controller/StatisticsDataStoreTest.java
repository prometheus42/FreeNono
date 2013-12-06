/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 by FreeNono Development Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *****************************************************************************/
package org.freenono.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests class that stores statistics data for all nonograms and for overall
 * statistics.
 * 
 * @author Christian Wichmann
 */
public class StatisticsDataStoreTest {

	private static final String TEST_HASH_1 = "267e850308ef27f0a9c1857792d2faac";
	private static final String TEST_HASH_2 = "267e85030efefababababab792d2faac";

	private static final String statisticsFile = "data" + File.separator
			+ "statistics" + File.separator + "statistics.xml";

	private static StatisticsDataStore dataStore;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	/**
	 * Sets up a data store instance via static call on the Singleton with a
	 * given example statistics file in XML format.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		dataStore = StatisticsDataStore.getInstance(statisticsFile);
	}

	/**
	 * Drops existing data store instance.
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		dataStore = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#getTimesPlayedForNonogram(java.lang.String)}
	 * .
	 */
	@Test
	public final void testGetTimesPlayedForNonogram() {

		assertEquals("Number of times that nonogram has been played is wrong.",
				dataStore.getTimesPlayedForNonogram(TEST_HASH_1), 12);
		assertEquals("Number of times that nonogram has been played is wrong.",
				dataStore.getTimesPlayedForNonogram(TEST_HASH_2), 44);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#getTimesWonForNonogram(java.lang.String)}
	 * .
	 */
	@Test
	public final void testGetTimesWonForNonogram() {

		int countWon = dataStore.getTimesWonForNonogram(TEST_HASH_1);

		assertEquals("Number of times that nonogram has been won is wrong.",
				countWon, 87);

		countWon = dataStore.getTimesWonForNonogram(TEST_HASH_2);

		assertEquals("Number of times that nonogram has been won is wrong.",
				countWon, 444);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#incrementTimesPlayedForNonogram(java.lang.String)}
	 * .
	 */
	@Test
	public final void testIncrementTimesPlayedForNonogram() {

		dataStore.incrementTimesPlayedForNonogram(TEST_HASH_1);
		assertEquals("Number of times that nonogram has been played is wrong.",
				dataStore.getTimesPlayedForNonogram(TEST_HASH_1), 13);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#incrementTimesWonForNonogram(java.lang.String)}
	 * .
	 */
	@Test
	public final void testIncrementTimesWonForNonogram() {

		dataStore.incrementTimesWonForNonogram(TEST_HASH_1);
		assertEquals("Number of times that nonogram has been played is wrong.",
				dataStore.getTimesWonForNonogram(TEST_HASH_1), 88);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#getFieldsCorrectlyOccupied()}
	 * .
	 */
	@Test
	public final void testGetFieldsCorrectlyOccupied() {

		assertEquals("Number of overall correctly occupied fields is wrong.",
				dataStore.getFieldsCorrectlyOccupied(), 65465);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#getFieldsWronglyOccupied()}
	 * .
	 */
	@Test
	public final void testGetFieldsWronglyOccupied() {

		assertEquals("Number of overall wrongly occupied fields is wrong.",
				dataStore.getFieldsWronglyOccupied(), 98765);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#getFieldsMarked()}.
	 */
	@Test
	public final void testGetFieldsMarked() {

		assertEquals("Number of overall marked fields is wrong.",
				dataStore.getFieldsMarked(), 12345);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#incrementFieldsCorrectlyOccupied()}
	 * .
	 */
	@Test
	public final void testIncrementFieldsCorrectlyOccupied() {

		dataStore.incrementFieldsCorrectlyOccupied();
		assertEquals("Number of overall correctly occupied fields is wrong.",
				dataStore.getFieldsCorrectlyOccupied(), 65466);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#incrementFieldsWronglyOccupied()}
	 * .
	 */
	@Test
	public final void testIncrementFieldsWronglyOccupied() {

		dataStore.incrementFieldsWronglyOccupied();
		assertEquals("Number of overall wrongly occupied fields is wrong.",
				dataStore.getFieldsWronglyOccupied(), 98766);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#incrementFieldsMarked()}
	 * .
	 */
	@Test
	public final void testIncrementFieldsMarked() {

		dataStore.incrementFieldsMarked();
		assertEquals("Number of overall marked fields is wrong.",
				dataStore.getFieldsMarked(), 12346);
	}

	/**
	 * Test method for
	 * {@link org.freenono.controller.StatisticsDataStore#saveStatisticsToFile(java.io.File)}
	 * .
	 */
	@Test
	public final void testSaveStatisticsToFile() {

		// store old values
		int co = dataStore.getFieldsCorrectlyOccupied();
		int wo = dataStore.getFieldsWronglyOccupied();
		int ma = dataStore.getFieldsMarked();
		int nw = dataStore.getTimesWonForNonogram(TEST_HASH_1);
		int np = dataStore.getTimesPlayedForNonogram(TEST_HASH_1);
		int nl = dataStore.getTimesLostForNonogram(TEST_HASH_1);

		// store values in temp file
		File createdFile = null;
		try {
			createdFile = tempFolder.newFile();
		} catch (IOException e) {
			System.out.println("Could not create temporary file.");
		}
		dataStore.saveStatisticsToFile(createdFile);

		// check for correct values
		StatisticsDataStore newDataStore = StatisticsDataStore
				.getInstance(createdFile.getAbsolutePath());
		assertEquals("Value not correctly saved and loaded again.",
				newDataStore.getFieldsCorrectlyOccupied(), co);
		assertEquals("Value not correctly saved and loaded again.",
				newDataStore.getFieldsWronglyOccupied(), wo);
		assertEquals("Value not correctly saved and loaded again.",
				newDataStore.getFieldsMarked(), ma);
		assertEquals("Value not correctly saved and loaded again.",
				newDataStore.getTimesWonForNonogram(TEST_HASH_1), nw);
		assertEquals("Value not correctly saved and loaded again.",
				newDataStore.getTimesPlayedForNonogram(TEST_HASH_1), np);
		assertEquals("Value not correctly saved and loaded again.",
				newDataStore.getTimesLostForNonogram(TEST_HASH_1), nl);
	}
}

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
package org.freenono.serializer;

import static org.junit.Assert.*;

import java.io.File;

import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;
import org.freenono.serializer.data.XMLNonogramSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the serializer for the xml based file format.
 * 
 * @author Markus Wichmann
 */
public class XMLNonogramSerializerTest {

	private static final File rootDir = new File("data" + File.separator
			+ "nonogram" + File.separator + "xml");

	private static final File xSingle01File = new File(rootDir,
			"Single01.nonogram");
	private static final File xMulti01File = new File(rootDir,
			"Multi01.nonogram");
	private static final File xMultiDim01File = new File(rootDir,
			"MultiDim01.nonogram");

	private XMLNonogramSerializer xmlNS = null;

	@Before
	public void setUp() throws Exception {

		xmlNS = new XMLNonogramSerializer();

	}

	@After
	public void tearDown() throws Exception {

		xmlNS = null;

	}

	/* testing xml nonogram serializer */

	@Test
	public void testSingle() {

		try {

			Nonogram[] n = xmlNS.load(xSingle01File);

			assertTrue(n.length == 1);

			NonogramTestHelper.checkNonogram(n[0], "Test", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross01Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}

	@Test
	public void testMulti() {

		try {

			Nonogram[] n = xmlNS.load(xMulti01File);

			assertTrue(n.length == 2);

			NonogramTestHelper.checkNonogram(n[0], "Test1", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross01Field);
			NonogramTestHelper.checkNonogram(n[1], "Test2", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross02Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}

	@Test
	public void testDimension() {

		try {

			Nonogram[] n = xmlNS.load(xMultiDim01File);

			assertTrue(n.length == 2);

			NonogramTestHelper.checkNonogram(n[0], "Test1", "",
					DifficultyLevel.UNDEFINED, 3, 5,
					NonogramTestHelper.cross03Field);
			NonogramTestHelper.checkNonogram(n[1], "Test2", "",
					DifficultyLevel.UNDEFINED, 5, 3,
					NonogramTestHelper.cross04Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}

	// TODO test error cases
	// TODO test additional fields
	// TODO test if optional fields are missing
}

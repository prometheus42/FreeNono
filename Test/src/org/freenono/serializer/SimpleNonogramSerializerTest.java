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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests serializer for old simple nonogram file format based on text files.
 * 
 * @author Markus Wichmann
 */
public class SimpleNonogramSerializerTest {

	private static final File rootDir = new File("data" + File.separator
			+ "nonogram" + File.separator + "simple");

	private static final File sLineEnding01File = new File(rootDir,
			"LineEnding01.nono");
	private static final File sLineEnding02File = new File(rootDir,
			"LineEnding02.nono");
	private static final File sMultiLineEnding01File = new File(rootDir,
			"MultiLineEnding01.nono");
	private static final File sMultiLineEnding02File = new File(rootDir,
			"MultiLineEnding02.nono");
	private static final File sMultiDim01File = new File(rootDir,
			"MultiDim01.nono");

	private SimpleNonogramSerializer simpleNS = null;

	@Before
	public void setUp() throws Exception {

		simpleNS = new SimpleNonogramSerializer();
	}

	@After
	public void tearDown() throws Exception {

		simpleNS = null;
	}

	/* testing simple nonogram serializer */

	@Test
	public void testLineEnding() {

		try {

			Nonogram[] n1 = simpleNS.load(sLineEnding01File);
			Nonogram[] n2 = simpleNS.load(sLineEnding02File);

			assertTrue(n1.length == 1);
			assertTrue(n2.length == 1);

			System.out.println(n1[0].getDescription()+" . "+n2[0]);
			NonogramTestHelper.checkNonogram(n1[0], "Test", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross01Field);
			NonogramTestHelper.checkNonogram(n2[0], "Test", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross01Field);

		} catch (Exception e) {

			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testMultiLineEnding() {

		try {

			Nonogram[] n1 = simpleNS.load(sMultiLineEnding01File);
			Nonogram[] n2 = simpleNS.load(sMultiLineEnding02File);

			assertTrue(n1.length == 2);
			assertTrue(n2.length == 2);

			NonogramTestHelper.checkNonogram(n1[0], "Test1", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross01Field);
			NonogramTestHelper.checkNonogram(n2[0], "Test1", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross01Field);

			NonogramTestHelper.checkNonogram(n1[1], "Test2", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross02Field);
			NonogramTestHelper.checkNonogram(n2[1], "Test2", "",
					DifficultyLevel.UNDEFINED, 3, 3,
					NonogramTestHelper.cross02Field);

		} catch (Exception e) {

			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testDimension() {

		try {

			Nonogram[] n = simpleNS.load(sMultiDim01File);

			assertTrue(n.length == 2);

			NonogramTestHelper.checkNonogram(n[0], "Test1", "",
					DifficultyLevel.UNDEFINED, 3, 5,
					NonogramTestHelper.cross03Field);
			NonogramTestHelper.checkNonogram(n[1], "Test2", "",
					DifficultyLevel.UNDEFINED, 5, 3,
					NonogramTestHelper.cross04Field);

		} catch (Exception e) {

			e.printStackTrace();
			assertTrue(false);
		}
	}

	// TODO test error cases
}

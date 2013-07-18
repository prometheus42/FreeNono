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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.freenono.model.data.Nonogram;
import org.freenono.serializer.data.SimpleNonogramSerializer;
import org.freenono.serializer.data.XMLNonogramSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the serializer for the old plain text nonogram file format.
 * 
 * @author Markus Wichmann
 */
public class NonogramSerializerTest {

	private static final File rootDir = new File("data" + File.separator
			+ "nonogram" + File.separator);

	private static final File simpleDir = new File(rootDir, "simple");
	private static final File xmlDir = new File(rootDir, "xml");

	private static final File sMultiDim01File = new File(simpleDir,
			"MultiDim01.nono");

	private static final File xMultiDim01File = new File(xmlDir,
			"MultiDim01.nonogram");

	private SimpleNonogramSerializer simpleNS = null;
	private XMLNonogramSerializer xmlNS = null;

	@Before
	public void setUp() throws Exception {

		simpleNS = new SimpleNonogramSerializer();
		xmlNS = new XMLNonogramSerializer();

	}

	@After
	public void tearDown() throws Exception {

		simpleNS = null;
		xmlNS = null;

	}

	/* testing nonogram serializer cooperation */

	@Test
	public void testCoop() {

		try {

			Nonogram[] xmlN = xmlNS.load(xMultiDim01File);
			Nonogram[] simpleN = simpleNS.load(sMultiDim01File);

			NonogramTestHelper.compareNonogramList(xmlN, simpleN, false);

		} catch (Exception e) {
			assertTrue(false);
		}

	}

	// TODO multiple load & saves
	// TODO test error cases
}

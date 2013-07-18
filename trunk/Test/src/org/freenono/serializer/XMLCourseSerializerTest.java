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
import java.io.IOException;

import org.freenono.model.data.Course;
import org.freenono.serializer.data.CourseFormatException;
import org.freenono.serializer.data.NonogramFormatException;
import org.freenono.serializer.data.XMLCourseSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the serializer for xml courses.
 * 
 * @author Markus Wichmann
 */
public class XMLCourseSerializerTest {

	private static final File rootDir = new File("data" + File.separator
			+ "course" + File.separator);

	private static final File empty01Dir = new File(rootDir, "Empty01");
	private static final File single01Dir = new File(rootDir, "Single01");
	private static final File multi01Dir = new File(rootDir, "Multi01");
	private static final File mixed01Dir = new File(rootDir, "Mixed01");

	private XMLCourseSerializer xmlCS = null;

	@Before
	public void setUp() throws Exception {

		xmlCS = new XMLCourseSerializer();

	}

	@After
	public void tearDown() throws Exception {

		xmlCS = null;

	}

	/* Directory courses */

	@Test
	public void testEmptyCourse() {

		try {

			xmlCS.load(empty01Dir);
			assertTrue("expected exception wasn't thrown", false);

		} catch (NullPointerException e) {
			assertTrue("unexpected NullPointerException was thrown", false);
		} catch (IOException e) {
			assertTrue("unexpected IOException was thrown", false);
		} catch (NonogramFormatException e) {
			assertTrue("unexpected NonogramFormatException was thrown", false);
		} catch (CourseFormatException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testSingleCourse() {

		try {

			Course c = xmlCS.load(single01Dir);
			CourseTestHelper.checkCourse(c, "Single01", 1);

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testMultiCourse() {

		try {

			Course c = xmlCS.load(multi01Dir);
			CourseTestHelper.checkCourse(c, "Multi01", 2);

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testMixedCourse() {

		try {

			Course c = xmlCS.load(mixed01Dir);
			CourseTestHelper.checkCourse(c, "Mixed01", 2);

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	// TODO test error cases
}

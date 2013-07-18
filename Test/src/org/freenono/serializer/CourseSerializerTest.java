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

import org.freenono.model.data.Course;
import org.freenono.serializer.data.XMLCourseSerializer;
import org.freenono.serializer.data.ZipCourseSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests serializer to courses as xml files in a directory or into a nonopack
 * file.
 * 
 * @author Markus Wichmann
 */
public class CourseSerializerTest {

	private static final File rootDir = new File("data" + File.separator
			+ "course" + File.separator);

	// private static final File empty01Dir = new File(rootDir, "Empty01");
	private static final File single01Dir = new File(rootDir, "Single01");
	private static final File multi01Dir = new File(rootDir, "Multi01");
	private static final File mixed01Dir = new File(rootDir, "Mixed01");

	// private static final File empty01File = new File(rootDir,
	// "Empty01.nonopack");
	private static final File single01File = new File(rootDir,
			"Single01.nonopack");
	private static final File multi01File = new File(rootDir,
			"Multi01.nonopack");
	private static final File mixed01File = new File(rootDir,
			"Mixed01.nonopack");

	private XMLCourseSerializer xmlCS = null;
	private ZipCourseSerializer zipCS = null;

	@Before
	public void setUp() throws Exception {

		xmlCS = new XMLCourseSerializer();
		zipCS = new ZipCourseSerializer();

	}

	@After
	public void tearDown() throws Exception {

		xmlCS = null;
		zipCS = null;

	}

	@Test
	public void testCoopSingleCourse() {

		try {

			Course c1 = xmlCS.load(single01Dir);
			Course c2 = zipCS.load(single01File);

			CourseTestHelper.compareCourse(c1, c2, false);

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testCoopMultiCourse() {

		try {

			Course c1 = xmlCS.load(multi01Dir);
			Course c2 = zipCS.load(multi01File);

			CourseTestHelper.compareCourse(c1, c2, false);

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testCoopMixedCourse() {

		try {

			Course c1 = xmlCS.load(mixed01Dir);
			Course c2 = zipCS.load(mixed01File);

			CourseTestHelper.compareCourse(c1, c2, false);

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	// TODO test error cases
}

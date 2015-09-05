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

import org.freenono.model.data.Course;

/**
 * Helper class for course serializer tests.
 * 
 * @author Markus Wichmann
 */
public class CourseTestHelper {

	public static void checkCourse(Course c, String name, int nonogramCount) {

		assertTrue("wrong name", c.getName().equals(name));
		assertTrue("wrong number of nonograms",
				c.getNonogramCount() == nonogramCount);

	}

	public static void compareCourse(Course c1, Course c2,
			boolean checkOptFields) {

		assertTrue("different names", c1.getName().equals(c2.getName()));
		assertTrue("different number of nonograms",
				c1.getNonogramCount() == c1.getNonogramCount());

		for (int i = 0; i < c1.getNonogramCount(); i++) {
			NonogramTestHelper.compareNonograms(c1.getNonogram(i),
					c2.getNonogram(i), checkOptFields);
		}

	}

	// TODO test sort order, even for the same names but different data
}

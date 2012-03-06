package org.freenono.serializer;

import static org.junit.Assert.assertTrue;

import org.freenono.model.Course;
import org.freenono.model.Nonogram;

/**
 * @author Markus Wichmann
 * 
 */
public class CourseTestHelper {

	public static void checkCourse(Course c, String name, int nonogramCount) {

		assertTrue("wrong name", c.getName().equals(name));
		assertTrue("wrong number of nonograms", c.getNonogramCount() == nonogramCount);

	}

	public static void compareCourse(Course c1, Course c2, boolean checkOptFields) {

		assertTrue("different names", c1.getName().equals(c2.getName()));
		assertTrue("different number of nonograms", c1.getNonogramCount() == c1.getNonogramCount());

		for (int i = 0; i < c1.getNonogramCount(); i++) {
			NonogramTestHelper.compareNonograms(c1.getNonogram(i), c2.getNonogram(i), checkOptFields);
		}

	}
	
	// TODO test sort order, even for the same names but different data

}

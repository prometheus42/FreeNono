package org.freenono.serializer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.freenono.model.data.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Markus Wichmann
 *
 */
public class ZipCourseSerializerTest {

	private static final File rootDir = new File("data" + File.separator + "course" + File.separator);

	private static final File empty01File = new File(rootDir, "Empty01.nonopack");
	private static final File single01File = new File(rootDir, "Single01.nonopack");
	private static final File multi01File = new File(rootDir, "Multi01.nonopack");
	private static final File mixed01File = new File(rootDir, "Mixed01.nonopack");

	private ZipCourseSerializer zipCS = null;

	@Before
	public void setUp() throws Exception {
		
		zipCS = new ZipCourseSerializer();
		
	}

	@After
	public void tearDown() throws Exception {

		zipCS = null;

	}

	
	
	/* zip courses */
	
	@Test
	public void testEmptyCourse() {

		try {
			
			zipCS.load(empty01File);
			assertTrue("expected exception wasn't thrown", false);
			
		} catch (NullPointerException e) {
			assertTrue("unexpected NullPointerException was thrown", false);
		} catch (IOException e) {
			assertTrue(true);
		} catch (NonogramFormatException e) {
			assertTrue("unexpected NonogramFormatException was thrown", false);
		} catch (CourseFormatException e) {
			assertTrue(true);
		}
		
	}

	@Test
	public void testSingleCourse() {

		try {
			
			Course c = zipCS.load(single01File);
			CourseTestHelper.checkCourse(c, "Single01", 1);
			
		} catch (Exception e) {
			
			assertTrue(false);
		}

	}

	@Test
	public void testMultiCourse() {

		try {
			
			Course c = zipCS.load(multi01File);
			CourseTestHelper.checkCourse(c, "Multi01", 2);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}
	
	@Test
	public void testMixedCourse() {

		try {
			
			Course c = zipCS.load(mixed01File);
			CourseTestHelper.checkCourse(c, "Mixed01", 2);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	// TODO test error cases
	
}

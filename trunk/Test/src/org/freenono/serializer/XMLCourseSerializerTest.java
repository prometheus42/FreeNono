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
public class XMLCourseSerializerTest {

	private static final File rootDir = new File("data" + File.separator + "course" + File.separator);

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

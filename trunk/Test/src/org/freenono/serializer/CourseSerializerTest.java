package org.freenono.serializer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.freenono.model.Course;
import org.freenono.serializer.XMLCourseSerializer;
import org.freenono.serializer.ZipCourseSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Markus Wichmann
 * 
 */
public class CourseSerializerTest {

	private static final File rootDir = new File("data" + File.separator + "course" + File.separator);

	private static final File empty01Dir = new File(rootDir, "Empty01");
	private static final File single01Dir = new File(rootDir, "Single01");
	private static final File multi01Dir = new File(rootDir, "Multi01");
	private static final File mixed01Dir = new File(rootDir, "Mixed01");

	private static final File empty01File = new File(rootDir, "Empty01.nonopack");
	private static final File single01File = new File(rootDir, "Single01.nonopack");
	private static final File multi01File = new File(rootDir, "Multi01.nonopack");
	private static final File mixed01File = new File(rootDir, "Mixed01.nonopack");

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

	
	
	/* Directory courses */
	
	@Test
	public void testXMLEmptyCourse() {
		
		try {
			
			Course c = xmlCS.load(empty01Dir);
			
			assertNotNull(c);
			assertTrue(c.getName().equals("Empty01"));
			assertTrue(c.getNonogramCount() == 0);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testXMLSingleCourse() {

		try {
			
			Course c = xmlCS.load(single01Dir);
			
			assertNotNull(c);
			assertTrue(c.getName().equals("Single01"));
			assertTrue(c.getNonogramCount() == 1);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testXMLMultiCourse() {

		try {
			
			Course c = xmlCS.load(multi01Dir);
			
			assertNotNull(c);
			assertTrue(c.getName().equals("Multi01"));
			assertTrue(c.getNonogramCount() == 2);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}
	
	@Test
	public void testXMLMixedCourse() {

		try {
			
			Course c = xmlCS.load(mixed01Dir);
			
			assertNotNull(c);
			assertTrue(c.getName().equals("Mixed01"));
			assertTrue(c.getNonogramCount() == 2);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	
	
	/* zip courses */
	
	@Test
	public void testZipEmptyCourse() {

		try {
			
			zipCS.load(empty01File);
			
			// make the test fail, because a zip file without entry isn't valid
			assertTrue(false);
			
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testZipSingleCourse() {

		try {
			
			Course c = zipCS.load(single01File);
			
			assertNotNull(c);
			assertTrue(c.getName().equals("Single01"));
			assertTrue(c.getNonogramCount() == 1);
			
		} catch (Exception e) {
			
			assertTrue(false);
		}

	}

	@Test
	public void testZipMultiCourse() {

		try {
			
			Course c = zipCS.load(multi01File);
			
			assertNotNull(c);
			assertTrue(c.getName().equals("Multi01"));
			assertTrue(c.getNonogramCount() == 2);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}
	
	@Test
	public void testZipMixedCourse() {

		try {
			
			Course c = zipCS.load(mixed01File);
			
			assertNotNull(c);
			assertTrue(c.getName().equals("Mixed01"));
			assertTrue(c.getNonogramCount() == 2);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

}

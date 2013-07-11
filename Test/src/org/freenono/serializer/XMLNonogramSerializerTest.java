package org.freenono.serializer;

import static org.junit.Assert.*;

import java.io.File;

import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Markus Wichmann
 *
 */
public class XMLNonogramSerializerTest {

	private static final File rootDir = new File("data" + File.separator
			+ "nonogram" + File.separator + "xml");
	
	private static final File xSingle01File = new File(rootDir, "Single01.nonogram");
	private static final File xMulti01File = new File(rootDir, "Multi01.nonogram");
	private static final File xMultiDim01File = new File(rootDir, "MultiDim01.nonogram");

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
			
			NonogramTestHelper.checkNonogram(n[0], "Test", "", DifficultyLevel.UNDEFINED, 3, 3, NonogramTestHelper.cross01Field);

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
			
			NonogramTestHelper.checkNonogram(n[0], "Test1", "", DifficultyLevel.UNDEFINED, 3, 3, NonogramTestHelper.cross01Field);
			NonogramTestHelper.checkNonogram(n[1], "Test2", "", DifficultyLevel.UNDEFINED, 3, 3, NonogramTestHelper.cross02Field);

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
			
			NonogramTestHelper.checkNonogram(n[0], "Test1", "", DifficultyLevel.UNDEFINED, 3, 5, NonogramTestHelper.cross03Field);
			NonogramTestHelper.checkNonogram(n[1], "Test2", "", DifficultyLevel.UNDEFINED, 5, 3, NonogramTestHelper.cross04Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}

	// TODO test error cases
	// TODO test additional fields
	// TODO test if optional fields are missing

}

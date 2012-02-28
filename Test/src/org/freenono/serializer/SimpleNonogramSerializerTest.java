package org.freenono.serializer;

import static org.junit.Assert.*;

import java.io.File;

import org.freenono.model.Nonogram;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Markus Wichmann
 *
 */
public class SimpleNonogramSerializerTest {

	private static final File rootDir = new File("data" + File.separator
			+ "nonogram" + File.separator + "simple");
	
	private static final File sLineEnding01File = new File(rootDir, "LineEnding01.nono");
	private static final File sLineEnding02File = new File(rootDir, "LineEnding02.nono");
	private static final File sMultiLineEnding01File = new File(rootDir, "MultiLineEnding01.nono");
	private static final File sMultiLineEnding02File = new File(rootDir, "MultiLineEnding02.nono");
	private static final File sMultiDim01File = new File(rootDir, "MultiDim01.nono");
	
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
			
			NonogramTestHelper.checkNonogram(n1[0], "Test", "", 0, 3, 3, NonogramTestHelper.cross01Field);
			NonogramTestHelper.checkNonogram(n2[0], "Test", "", 0, 3, 3, NonogramTestHelper.cross01Field);

		} catch (Exception e) {
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
			
			NonogramTestHelper.checkNonogram(n1[0], "Test1", "", 0, 3, 3, NonogramTestHelper.cross01Field);
			NonogramTestHelper.checkNonogram(n2[0], "Test1", "", 0, 3, 3, NonogramTestHelper.cross01Field);
			
			NonogramTestHelper.checkNonogram(n1[1], "Test2", "", 0, 3, 3, NonogramTestHelper.cross02Field);
			NonogramTestHelper.checkNonogram(n2[1], "Test2", "", 0, 3, 3, NonogramTestHelper.cross02Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}
	
	@Test
	public void testDimension() {

		try {
			
			Nonogram[] n = simpleNS.load(sMultiDim01File);
			
			assertTrue(n.length == 2);
			
			NonogramTestHelper.checkNonogram(n[0], "Test1", "", 0, 3, 5, NonogramTestHelper.cross03Field);
			NonogramTestHelper.checkNonogram(n[1], "Test2", "", 0, 5, 3, NonogramTestHelper.cross04Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}

	// TODO test error cases

}

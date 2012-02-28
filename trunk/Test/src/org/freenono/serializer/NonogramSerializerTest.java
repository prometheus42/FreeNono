package org.freenono.serializer;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.freenono.model.Nonogram;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Markus Wichmann
 * 
 */
public class NonogramSerializerTest {

	private static final File rootDir = new File("data"+File.separator+"nonogram/");
	
	private static final File simpleDir = new File(rootDir, "simple");
	private static final File xmlDir = new File(rootDir, "xml");
	
	private static final File sLineEnding01File = new File(simpleDir, "LineEnding01.nono");
	private static final File sLineEnding02File = new File(simpleDir, "LineEnding02.nono");
	private static final File sMultiLineEnding01File = new File(simpleDir, "MultiLineEnding01.nono");
	private static final File sMultiLineEnding02File = new File(simpleDir, "MultiLineEnding02.nono");
	private static final File sMultiDim01File = new File(simpleDir, "MultiDim01.nono");
	
	private static final File xSingle01File = new File(xmlDir, "Single01.nonogram");
	private static final File xMulti01File = new File(xmlDir, "Multi01.nonogram");
	private static final File xMultiDim01File = new File(xmlDir, "MultiDim01.nonogram");

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

	
	
	/* testing simple nonogram serializer */
	
	@Test
	public void testSimpleLineEnding() {

		try {
			
			Nonogram[] n1 = simpleNS.load(sLineEnding01File);
			Nonogram[] n2 = simpleNS.load(sLineEnding02File);
			
			assertTrue(n1.length == 1);
			assertTrue(n2.length == 1);
			
			NonogramHelper.checkNonogram(n1[0], "Test", "", 0, 3, 3, NonogramHelper.cross01Field);
			NonogramHelper.checkNonogram(n2[0], "Test", "", 0, 3, 3, NonogramHelper.cross01Field);

		} catch (Exception e) {
			assertTrue(false);
		}

	}
	
	@Test
	public void testSimpleMultiLineEnding() {

		try {
			
			Nonogram[] n1 = simpleNS.load(sMultiLineEnding01File);
			Nonogram[] n2 = simpleNS.load(sMultiLineEnding02File);
			
			assertTrue(n1.length == 2);
			assertTrue(n2.length == 2);
			
			NonogramHelper.checkNonogram(n1[0], "Test1", "", 0, 3, 3, NonogramHelper.cross01Field);
			NonogramHelper.checkNonogram(n2[0], "Test1", "", 0, 3, 3, NonogramHelper.cross01Field);
			
			NonogramHelper.checkNonogram(n1[1], "Test2", "", 0, 3, 3, NonogramHelper.cross02Field);
			NonogramHelper.checkNonogram(n2[1], "Test2", "", 0, 3, 3, NonogramHelper.cross02Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}
	
	@Test
	public void testSimpleDimension() {

		try {
			
			Nonogram[] n = simpleNS.load(sMultiDim01File);
			
			assertTrue(n.length == 2);
			
			NonogramHelper.checkNonogram(n[0], "Test1", "", 0, 3, 5, NonogramHelper.cross03Field);
			NonogramHelper.checkNonogram(n[1], "Test2", "", 0, 5, 3, NonogramHelper.cross04Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}

	
	
	/* testing xml nonogram serializer */
	
	
	@Test
	public void testXMLSingle() {

		try {
			
			Nonogram[] n = xmlNS.load(xSingle01File);
			
			assertTrue(n.length == 1);
			
			NonogramHelper.checkNonogram(n[0], "Test", "", 0, 3, 3, NonogramHelper.cross01Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}
	
	@Test
	public void testXMLMulti() {

		try {
			
			Nonogram[] n = xmlNS.load(xMulti01File);
			
			assertTrue(n.length == 2);
			
			NonogramHelper.checkNonogram(n[0], "Test1", "", 0, 3, 3, NonogramHelper.cross01Field);
			NonogramHelper.checkNonogram(n[1], "Test2", "", 0, 3, 3, NonogramHelper.cross02Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}
	
	@Test
	public void testXMLDimension() {

		try {
			
			Nonogram[] n = xmlNS.load(xMultiDim01File);
			
			assertTrue(n.length == 2);
			
			NonogramHelper.checkNonogram(n[0], "Test1", "", 0, 3, 5, NonogramHelper.cross03Field);
			NonogramHelper.checkNonogram(n[1], "Test2", "", 0, 5, 3, NonogramHelper.cross04Field);

		} catch (Exception e) {
			System.out.println(e);
			assertTrue(false);
		}

	}

	
	// TODO test additional fields
	// TODO test if optional fields are missing

}

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

	private static final File rootDir = new File("data" + File.separator
			+ "nonogram" + File.separator);
	
	private static final File simpleDir = new File(rootDir, "simple");
	private static final File xmlDir = new File(rootDir, "xml");
	
	private static final File sMultiDim01File = new File(simpleDir, "MultiDim01.nono");
	
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

	
	
	/* testing nonogram serializer cooperation */
	
	@Test
	public void testCoop() {

		try {
			
			Nonogram[] xmlN = xmlNS.load(xMultiDim01File);
			Nonogram[] simpleN = simpleNS.load(sMultiDim01File);
			
			NonogramTestHelper.compareNonogramList(xmlN, simpleN, false);

		} catch (Exception e) {
			assertTrue(false);
		}

	}
	
	
	// TODO multiple load & saves
	// TODO test error cases
	

}

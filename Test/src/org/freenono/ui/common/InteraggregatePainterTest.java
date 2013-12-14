/**
 * 
 */
package org.freenono.ui.common;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christian Wichmann
 * 
 */
public class InteraggregatePainterTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.freenono.ui.common.InteraggregatePainter#resetImage()}.
	 */
	@Test
	public final void testResetImage() {

		InteraggregatePainter ip = new InteraggregatePainter();
		checkImage(ip.getImage());
		ip.doIterations(50);
		ip.resetImage();
		checkImage(ip.getImage());
	}

	private void checkImage(BufferedImage image) {

		int pixelData = 0;

		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				pixelData += image.getRGB(j, i) + 1;
			}
		}

		assertEquals("Image is not empty.", pixelData, 0);
	}

	/**
	 * Test method for
	 * {@link org.freenono.ui.common.InteraggregatePainter#doIterations(int)}.
	 */
	@Test
	public final void testDoIterations() {

		InteraggregatePainter ip = new InteraggregatePainter();
		BufferedImage i1 = ip.getImage();
		ip.doIterations(50);
		BufferedImage i2 = ip.getImage();
		compareImagesForInequality(i1, i2);
	}

	/**
	 * Tests whether two images are <b>not</b> equal but of the same size.
	 * 
	 * @param i1
	 *            image 1
	 * @param i2
	 *            image 2
	 */
	private void compareImagesForInequality(BufferedImage i1, BufferedImage i2) {

		assertEquals("Images do not have same height.", i1.getHeight(),
				i2.getHeight());
		assertEquals("Images do not have same width.", i1.getWidth(),
				i2.getWidth());

		boolean imagesAreDifferent = false;
		mainloop: for (int i = 0; i < i1.getHeight(); i++) {
			for (int j = 0; j < i1.getWidth(); j++) {
				if (i1.getRGB(j, i) != i2.getRGB(j, i)) {
					imagesAreDifferent = true;
					break mainloop;
				}
			}
		}
		assertEquals("Image are the same.", imagesAreDifferent, true);
	}

	/**
	 * Tests whether two images are <b>equal</b> and of the same size.
	 * 
	 * @param i1
	 *            image 1
	 * @param i2
	 *            image 2
	 */
	private void compareImagesForEquality(BufferedImage i1, BufferedImage i2) {

		assertEquals("Images do not have same height.", i1.getHeight(),
				i2.getHeight());
		assertEquals("Images do not have same width.", i1.getWidth(),
				i2.getWidth());

		for (int i = 0; i < i1.getHeight(); i++) {
			for (int j = 0; j < i1.getWidth(); j++) {
				assertEquals("Image are different.", i1.getRGB(j, i),
						i2.getRGB(j, i));
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.freenono.ui.common.InteraggregatePainter#saveToFile(java.lang.String)}
	 * .
	 */
	@Test
	public final void testSaveToFile() {

		InteraggregatePainter ip = new InteraggregatePainter();
		File createdFile = null;

		try {
			createdFile = tempFolder.newFile();
			ip.saveToFile(createdFile.getCanonicalPath());
			assertEquals("", true,
					new File(createdFile.getCanonicalPath()).isFile());
		} catch (IOException e) {
			System.out.println("Could not create temporary file.");
		}
	}
}

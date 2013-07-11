/**
 * 
 */
package org.freenono.serializer;

import static org.junit.Assert.assertTrue;

import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;

/**
 * @author Markus Wichmann
 * 
 */
public class NonogramTestHelper {

	public static final boolean[][] cross01Field = { { false, true, false },
			{ true, true, true }, { false, true, false } };
	public static final boolean[][] cross02Field = { { true, false, true },
			{ false, true, false }, { true, false, true } };

	public static final boolean[][] cross03Field = { { false, true, false },
			{ false, true, false }, { true, true, true },
			{ false, true, false }, { false, true, false } };
	public static final boolean[][] cross04Field = {
			{ false, false, true, false, false },
			{ true, true, true, true, true },
			{ false, false, true, false, false } };

	public static void checkNonogram(Nonogram n, String name, String desc,
			DifficultyLevel diff, int width, int height, boolean[][] field) {

		assertTrue("wrong name", n.getName().equals(name));

		assertTrue("wrong description", n.getDescription().equals(desc));
		assertTrue("wrong difficulty", n.getDifficulty() == diff);

		assertTrue("wrong width", n.width() == width);
		assertTrue("wrong height", n.height() == height);

		for (int y = 0; y < n.height(); y++) {
			for (int x = 0; x < n.width(); x++) {
				assertTrue("wrong field value at position {" + x + ", " + y
						+ "}", n.getFieldValue(x, y) == field[y][x]);
			}
		}

	}

	public static void compareNonogramList(Nonogram[] n1, Nonogram[] n2,
			boolean checkOptFields) {

		assertTrue("different number of nonograms", n1.length == n2.length);

		for (int i = 0; i < n1.length; i++) {
			compareNonograms(n1[i], n2[i], checkOptFields);
		}

	}

	public static void compareNonograms(Nonogram n1, Nonogram n2,
			boolean checkOptFields) {

		assertTrue("different names", n1.getName().equals(n2.getName()));

		if (checkOptFields) {
			assertTrue("different descriptions",
					n1.getDescription().equals(n2.getDescription()));
			assertTrue("different difficulty",
					n1.getDifficulty() == n2.getDifficulty());
		}

		assertTrue("different width", n1.width() == n2.width());
		assertTrue("different height", n1.height() == n2.height());

		for (int y = 0; y < n1.height(); y++) {
			for (int x = 0; x < n1.width(); x++) {
				assertTrue("different field value at position {" + x + ", " + y
						+ "}", n1.getFieldValue(x, y) == n2.getFieldValue(x, y));
			}
		}

	}
}

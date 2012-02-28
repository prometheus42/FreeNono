/**
 * 
 */
package org.freenono.serializer;

import static org.junit.Assert.assertTrue;

import org.freenono.model.Nonogram;

/**
 * @author Markus Wichmann
 *
 */
public class NonogramHelper {

	public static final boolean[][] cross01Field = { { false, true, false }, { true, true, true }, { false, true, false } };
	public static final boolean[][] cross02Field = { { true, false, true }, { false, true, false }, { true, false, true } };
	
	public static final boolean[][] cross03Field = { { false, true, false }, { false, true, false }, { true, true, true }, { false, true, false }, { false, true, false } };
	public static final boolean[][] cross04Field = { { false, false, true, false, false }, { true, true, true, true, true }, { false, false, true, false, false } };
	
	public static void checkNonogram(Nonogram n, String name, String desc, int diff,
			int width, int height, boolean[][] field) {
		assertTrue(n.getName().equals(name));
		assertTrue(n.getDescription().equals(desc));
		assertTrue(n.getDifficulty() == diff);
		assertTrue(n.width() == width);
		assertTrue(n.height() == height);
		for (int y = 0; y < n.height(); y++) {
			for (int x = 0; x < n.width(); x++) {
				assertTrue(n.getFieldValue(x, y) == field[y][x]);
			}
		}
	}
}

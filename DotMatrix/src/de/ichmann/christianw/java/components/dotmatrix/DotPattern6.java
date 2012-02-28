package de.ichmann.christianw.java.components.dotmatrix;

/**
 * Dot pattern for the numeral 6
 * 
 * @author Christian Wichmann
 * @version 0.1
 */
public class DotPattern6 extends DotPattern {

	DotPattern6() {
		this.fillPattern();
	}

	/**
	 * Method must be overwritten by concrete pattern class to fill the array
	 */
	protected void fillPattern() {
		int[] list = new int[] { 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0,
				0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0 };

		int n = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				pattern[i][j] = (list[n++] != 0);
			}
		}
	}

	@Override
	public boolean[][] getPattern() {
		return pattern;
	}

	@Override
	public boolean getDot(int x, int y) {
		if (x < columns && y < rows)
			return pattern[y][x];
		else
			return false;
	}

	@Override
	public String toString() {
		return "6";
	}

}

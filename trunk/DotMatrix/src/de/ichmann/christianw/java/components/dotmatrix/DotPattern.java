package de.ichmann.christianw.java.components.dotmatrix;

/**
 * Abstract class for DotPattern
 * 
 * @author Christian Wichmann
 * @version 0.1
 */
public abstract class DotPattern {
	protected int rows = 8, columns = 6;
	protected boolean[][] pattern = new boolean[rows][columns]; // [Zeile][Spalte]

	/**
	 * Getter for pattern array with data for selected pattern
	 */
	public abstract boolean[][] getPattern();

	public abstract boolean getDot(int x, int y);

	/**
	 * Control method for choosing a pattern for drawing
	 */
	public static DotPattern selectPattern(char ch) {
		switch (ch) {
		case '0':
			return new DotPattern0();
		case '1':
			return new DotPattern1();
		case '2':
			return new DotPattern2();
		case '3':
			return new DotPattern3();
		case '4':
			return new DotPattern4();
		case '5':
			return new DotPattern5();
		case '6':
			return new DotPattern6();
		case '7':
			return new DotPattern7();
		case '8':
			return new DotPattern8();
		case '9':
			return new DotPattern9();
		case 'X':
			return new DotPatternX();
		case ':':
			return new DotPatternColon();
		default:
			return new DotPatternEmpty();
		}
	}
}

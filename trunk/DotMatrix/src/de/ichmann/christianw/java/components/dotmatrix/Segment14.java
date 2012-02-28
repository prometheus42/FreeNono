package de.ichmann.christianw.java.components.dotmatrix;

import java.awt.*;

public class Segment14 extends Canvas {

	private static final long serialVersionUID = 1L;

	private int polysx[][] = { { 5, 15, 15, 14, 14, 6 }, // Segment 0
			{ 16, 25, 25, 24, 24, 17 }, // Segment 1
			{ 25, 23, 23, 22, 22, 24 }, // Segment 2
			{ 23, 21, 21, 20, 20, 22 }, // Segment 3
			{ 20, 10, 10, 11, 11, 19 }, // Segment 4
			{ 9, 0, 0, 1, 1, 8 }, // Segment 5
			{ 0, 0, 0, 0, 0, 0 }, // Segment 6
			{ 0, 0, 0, 0, 0, 0 }, // Segment 7
			{ 0, 0, 0, 0, 0, 0 }, // Segment 8
			{ 0, 0, 0, 0, 0, 0 }, // Segment 9
			{ 0, 0, 0, 0, 0, 0 }, // Segment 10
			{ 0, 0, 0, 0, 0, 0 }, // Segment 11
			{ 0, 0, 0, 0, 0, 0 }, // Segment 12
			{ 0, 0, 0, 0, 0, 0 }, // Segment 13
			{ 0, 0, 0, 0, 0, 0 }, // Segment 14
			{ 0, 0, 0, 0, 0, 0 }, // Segment 15
	};
	private int polysy[][] = { { 0, 0, 0, 1, 1, 1 }, // Segment 0
			{ 0, 0, 0, 1, 1, 1 }, // Segment 1
			{ 1, 16, 16, 15, 15, 2 }, // Segment 2
			{ 17, 32, 32, 31, 31, 18 }, // Segment 3
			{ 32, 32, 32, 31, 31, 31 }, // Segment 4
			{ 32, 32, 32, 31, 31, 31 }, // Segment 5
			{ 0, 0, 0, 0, 0, 0 }, // Segment 6
			{ 0, 0, 0, 0, 0, 0 }, // Segment 7
			{ 0, 0, 0, 0, 0, 0 }, // Segment 8
			{ 0, 0, 0, 0, 0, 0 }, // Segment 9
			{ 0, 0, 0, 0, 0, 0 }, // Segment 10
			{ 0, 0, 0, 0, 0, 0 }, // Segment 11
			{ 0, 0, 0, 0, 8, 2 }, // Segment 12
			{ 0, 0, 0, 0, 0, 0 }, // Segment 13
			{ 0, 0, 0, 0, 0, 0 }, // Segment 14
			{ 0, 2, 0, 0, 0, 0 }, // Segment 15
	};
	private int characters[][] = {
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, // Ziffer 0
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // Ziffer 1
			{ 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0 }, // Ziffer 2
			{ 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0 }, // Ziffer 3
			{ 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0 }, // Ziffer 4
			{ 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0 }, // Ziffer 5
			{ 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0 }, // Ziffer 6
			{ 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 }, // Ziffer 7
			{ 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0 }, // Ziffer 8
			{ 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0 } // Ziffer 9
	};

	public Segment14() {
		this('0');
	}

	public Segment14(char character) {
		super();
	}

	public Dimension getPreferredSize() {
		return new Dimension(5 * 25, 5 * 32);
	}

	public Dimension getMinimumSize() {
		return new Dimension(1 * 25, 1 * 32);
	}

	public boolean isFocusTraversable() {
		return true;
	}

	public void paint(Graphics g) {
		Color darkred = new Color(127, 0, 0);
		Color lightred = new Color(255, 0, 0);
		//
		// calculate size and scale
		Dimension size = getSize();
		int dx = size.width / 25;
		int dy = size.height / 32;
		//
		// set background
		g.setColor(Color.gray);
		g.fillRect(0, 0, getSize().width, getSize().height);
		//
		// draw polygons
		g.setColor(lightred);
		for (int i = 0; i < 6; ++i) {
			if (characters[0][i] == 1) {
				Polygon poly = new Polygon();
				for (int j = 0; j < 6; ++j) {
					poly.addPoint(dx * polysx[i][j], dy * polysy[i][j]);
				}
				g.fillPolygon(poly);
			}
		}
	}

	public void paintBackup(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int width = 2;
		g2d.setStroke(new BasicStroke(width));
		Color darkred = new Color(127, 0, 0);
		Color lightred = new Color(255, 0, 0);
		//
		// calculate diameter
		Dimension size = getSize();
		int x = size.width / 6;
		int y = size.height / 8;
		int d = Math.min(x, y) / 10 * 7;
		//
		// draw segments as lines
		g2d.setColor(lightred);
		g2d.drawLine(6, 0, 12, 0);
		g2d.drawLine(15, 0, 23, 0);
		g2d.drawLine(24, 1, 22, 19);
		g2d.drawLine(22, 21, 20, 39);
		g2d.drawLine(19, 40, 11, 40);
		g2d.drawLine(9, 40, 1, 40);
		g2d.drawLine(0, 39, 2, 21);
		g2d.drawLine(2, 19, 4, 1);
		g2d.drawLine(3, 20, 9, 20);
		g2d.drawLine(11, 20, 21, 20);
	}

}

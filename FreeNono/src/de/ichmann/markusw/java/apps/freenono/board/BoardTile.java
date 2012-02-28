package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class BoardTile extends JComponent {

	private static final long serialVersionUID = -8166203161723979426L;

	private int TILE_WIDTH = 20;
	private int TILE_HEIGHT = 20;

	private Color selectionColor = new Color(150,150,150);
	private Color selectionColorActive = new Color(166, 143, 231);
	private Color fgColor = new Color(100, 100, 100);
	private Color textColor = Color.BLACK;
	private Color borderColor = Color.BLACK;
	private Color activeColor = new Color(166, 143, 231);

	private boolean marked = false;
	private boolean crossed = false;
	private boolean active = false;
	private boolean drawBorderNorth = false;
	private boolean drawBorderSouth = false;
	private boolean drawBorderWest = false;
	private boolean drawBorderEast = false;

	public static final int SELECTION_MARKER_RIGHT = 1;
	public static final int SELECTION_MARKER_LEFT = 2;
	public static final int SELECTION_MARKER_DOWN = 3;
	public static final int SELECTION_MARKER_UP = 4;
	private int selectionMarker = 0;
	private boolean selectionMarkerActive = false;

	private String label;
	private Font labelFont;

	public BoardTile(Dimension tileDimension) {
		super();
		TILE_WIDTH = (int) tileDimension.getWidth();
		TILE_HEIGHT = (int) tileDimension.getHeight();
		initialize();
	}

	private void initialize() {
		labelFont = new Font("FreeSans", Font.PLAIN, TILE_WIDTH / 2);
		label = new String();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);

		// paint active tile
		if (active) {
			g.setColor(activeColor);
			g.fillRect(2, 2, TILE_WIDTH - 4, TILE_HEIGHT - 4);
			if (marked) {
				g.setColor(fgColor);
				g.fillRect(4, 4, TILE_WIDTH - 8, TILE_HEIGHT - 8);
			}
		}

		// paint tile borders
		g.setColor(borderColor);
		if (drawBorderNorth) {
			g.drawLine(0, 0, TILE_WIDTH, 0);
		}
		if (drawBorderSouth) {
			g.drawLine(0, TILE_HEIGHT - 1, TILE_WIDTH, TILE_HEIGHT - 1);
		}
		if (drawBorderWest) {
			g.drawLine(0, 0, 0, TILE_HEIGHT);
		}
		if (drawBorderEast) {
			g.drawLine(TILE_WIDTH - 1, 0, TILE_WIDTH - 1, TILE_HEIGHT);
		}

		// paint marked tile
		if (marked) {
			g.setColor(fgColor);
			g.fillRect(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10);
		}

		// paint tile cross
		if (crossed) {
			g.setColor(borderColor);
			g.drawLine(3, 3, TILE_WIDTH - 4, TILE_HEIGHT - 4);
			g.drawLine(TILE_WIDTH - 4, 3, 3, TILE_HEIGHT - 4);
		}

		// paint tile label
		g.setColor(textColor);
		g.setFont(labelFont);
		switch (label.length()) {
		case 0:
			break;
		case 1:
			g.drawString(label, TILE_WIDTH / 2 - 8, TILE_HEIGHT / 2 + 7);
			break;
		case 2:
			g.drawString(label, TILE_WIDTH / 2 - 16, TILE_HEIGHT / 2 + 7);
			break;
		default:
			g.drawString(label, TILE_WIDTH / 2, TILE_HEIGHT / 2);
			break;
		}

		// build polygon and paint selection marker
		Polygon p1 = new Polygon();
		Polygon p2 = new Polygon();
		switch (selectionMarker) {
		case SELECTION_MARKER_RIGHT:
			p1.addPoint(TILE_WIDTH / 3, TILE_HEIGHT / 3);
			p1.addPoint(2 * TILE_WIDTH / 3, TILE_HEIGHT / 2);
			p1.addPoint(TILE_WIDTH / 3, 2 * TILE_HEIGHT / 3);
			p2.addPoint(TILE_WIDTH / 4, TILE_HEIGHT / 4);
			p2.addPoint(3 * TILE_WIDTH / 4, TILE_HEIGHT / 2);
			p2.addPoint(TILE_WIDTH / 4, 3 * TILE_HEIGHT / 4);
			break;
		case SELECTION_MARKER_DOWN:
			p1.addPoint(TILE_WIDTH / 3, TILE_HEIGHT / 3);
			p1.addPoint(2 * TILE_WIDTH / 3, TILE_HEIGHT / 3);
			p1.addPoint(TILE_WIDTH / 2, 2 * TILE_HEIGHT / 3);
			p2.addPoint(TILE_WIDTH / 4, TILE_HEIGHT / 4);
			p2.addPoint(3 * TILE_WIDTH / 4, TILE_HEIGHT / 4);
			p2.addPoint(TILE_WIDTH / 2, 3 * TILE_HEIGHT / 4);
			break;
		default:
			break;
		}
		if (selectionMarker != 0) {
			//g.setColor(selectionColor);
			//g.fillPolygon(p2);
			if (selectionMarkerActive) {
				g.setColor(selectionColorActive);
				g.fillPolygon(p2);
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		repaint();
	}

	public void setLabel(String x) {
		label = x;
		repaint();
	}

	public String getLabel() {
		return label;
	}

	public boolean isDrawBorderNorth() {
		return drawBorderNorth;
	}

	public void setDrawBorderNorth(boolean drawBorderNorth) {
		this.drawBorderNorth = drawBorderNorth;
		repaint();
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
			this.marked = marked;
			repaint();
	}

	public boolean isCrossed() {
		return crossed;
	}

	public void setCrossed(boolean crossed) {
			this.crossed = crossed;
			repaint();
	}

	public boolean isDrawBorderSouth() {
		return drawBorderSouth;
	}

	public void setDrawBorderSouth(boolean drawBorderSouth) {
		this.drawBorderSouth = drawBorderSouth;
		repaint();
	}

	public boolean isDrawBorderWest() {
		return drawBorderWest;
	}

	public void setDrawBorderWest(boolean drawBorderWest) {
		this.drawBorderWest = drawBorderWest;
		repaint();
	}

	public boolean isDrawBorderEast() {
		return drawBorderEast;
	}

	public void setDrawBorderEast(boolean drawBorderEast) {
		this.drawBorderEast = drawBorderEast;
		repaint();
	}

	public int getSelectionMarker() {
		return selectionMarker;
	}

	public void setSelectionMarker(int selectionMarker) {
		this.selectionMarker = selectionMarker;
		repaint();
	}

	public boolean isSelectionMarkerActive() {
		return selectionMarkerActive;
	}

	public void setSelectionMarkerActive(boolean selectionMarkerActive) {
		this.selectionMarkerActive = selectionMarkerActive;
		repaint();
	}

}

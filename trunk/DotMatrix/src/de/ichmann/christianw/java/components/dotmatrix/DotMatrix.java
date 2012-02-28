package de.ichmann.christianw.java.components.dotmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.util.List;
import java.util.ArrayList;

/**
 * Displays a DotMatrix in a JPanel component.
 * 
 * @author Christian Wichmann
 * @version 0.2
 * 
 *          TODO: Extract Timer and effect options to separate class
 *          DotMatrixControl DotMatrix x = new DotMatrix(115, 16);
 *          DotMatrixControl y = new DotMatrixControl(); y.addEmblem(new
 *          Emblem("1234567890", 24, 8));
 */
public class DotMatrix extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private int widthMatrix, heightMatrix;

	// constants for drawing the DotMatrix
	private Color inactiveColor = Color.BLACK;
	private Color activeColor = new Color(9, 255, 255);
	private Color backgroundColor = Color.BLACK;
	private Color shadowColor = Color.lightGray;
	private Color borderColor = Color.black;
	private boolean drawBorder = false;
	private boolean drawShadow = false;
	private boolean fillCircular = true;

	// data structures for DotMatrix
	private boolean[][] pattern;
	private Dot[][] dots;

	// data structures for emblem data
	private List<Emblem> emblems = new ArrayList<Emblem>(); // @jve:decl-index=0:
	private DotPattern currentCharacter;

	// timer for blinking and rotating text
	Timer t;

	public DotMatrix() {
		this(115, 16); // call Constructor with default dimensions
	}

	public DotMatrix(int w, int h) {
		super();
		this.widthMatrix = w;
		this.heightMatrix = h;
		this.setDoubleBuffered(true);
		this.setBackground(backgroundColor);
		if (drawBorder)
			setBorder(BorderFactory.createRaisedBevelBorder());
		// Define data structures
		dots = new Dot[widthMatrix][heightMatrix];
		pattern = new boolean[heightMatrix][widthMatrix];
		// Build pattern map for the first time
		buildMap();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(6 * widthMatrix, 6 * heightMatrix);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(3 * widthMatrix, 3 * heightMatrix);
	}

	@Override
	public boolean isFocusTraversable() {
		return false; // DotMatrix can not become focus owner
	}

	/*
	 * Build pattern map from DotPatternX controlled by Emblem data
	 */
	private void buildMap() {
		int offsetx, offsety, px, py;
		// clear pattern map
		clearMap();
		// Iterate over list of emblems
		for (Emblem e : emblems) {
			// use emblem only if visible (used for blinking text)
			if (!e.isVisible())
				continue;
			offsetx = e.getPosx();
			offsety = e.getPosy();
			// Iterate over characters of current emblem
			for (char ch : e.getText().toCharArray()) {
				currentCharacter = DotPattern.selectPattern(ch);
				// Iterate over dots in current character in current emblem
				for (int i = 0; i < 8; ++i) {
					for (int j = 0; j < 6; j++) {
						// Discard Dots out of range or "turn them around"
						if (fillCircular) {
							py = (i + offsety) % heightMatrix;
							px = (j + offsetx) % widthMatrix;
						} else {
							py = i + offsety;
							px = j + offsetx;
						}
						// Fill Dot only if in range of the DotMatrix
						if (px < widthMatrix && py < heightMatrix) {
							if (currentCharacter.getDot(j, i))
								pattern[py][px] = true;
							else
								pattern[py][px] = false;
						}
					}
				} // end character
				offsetx += 7;
				offsety += 0;
			} // end emblem
		} // end emblems
	}

	/*
	 * Delete the pattern array
	 */
	private void clearMap() {
		for (int i = 0; i < heightMatrix; ++i) {
			for (int j = 0; j < widthMatrix; j++) {
				pattern[i][j] = false;
			}
		}
	}

	/*
	 * Add an emblem to the DotMatrix
	 */
	public void addEmblem(Emblem e) {
		/*
		 * TODO: Check if emblem position is possible, throw exceptions if not
		 */
		emblems.add(e);
		if ((e.getOptions()) != 0) {
			t = new Timer(e.getDuration(), this);
			t.start();
		}
		refresh();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		//
		// calculate diameter and spacing
		Dimension size = getSize();
		int dx = size.width / widthMatrix;
		int dy = size.height / heightMatrix;
		int d = (int) (Math.min(dx, dy) / 10. * 9.);
		// Round: + 0.5 or Math.round(Math.min(dx, dy) / 10. * 7.);
		//
		// draw dots
		for (int i = 0; i < heightMatrix; ++i) {
			for (int j = 0; j < widthMatrix; j++) {
				dots[j][i] = new Dot(j * dx, i * dy, d);
				// draw shadow
				if (drawShadow) {
					g2d.setColor(shadowColor);
					g2d.fill(new Dot(j * dx + 2, i * dy + 2, d));
				}
				if (pattern[i][j])
					g2d.setColor(activeColor);
				else
					g2d.setColor(inactiveColor);
				g2d.fill(dots[j][i]);
				// if (drawOutline) {
				// g2d.setColor(Color.BLACK);
				// g2d.draw(dots[j][i]);
				// }
			}
		}
	}

	// import java.awt.image.BufferedImage;
	// private final GraphicsConfiguration gfxConf =
	// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	// private BufferedImage offImg;
	// @Override protected void paintComponent( Graphics g )
	// {
	// if ( offImg == null || offImg.getWidth() != getWidth() ||
	// offImg.getHeight() != getHeight() )
	// {
	// offImg = gfxConf.createCompatibleImage( getWidth(), getHeight() );
	// bigPaint( offImg.createGraphics() );
	// }
	// g.drawImage( offImg, 0, 0, this );
	// }

	/*
	 * refresh() can be called by other objects to rebuild the pattern map and
	 * repaint the entire panel.
	 */
	public void refresh() {
		buildMap();
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// if timer is elapsed...
		for (Emblem e : emblems) {
			// if no option is set...
			if ((e.getOptions() & Emblem.NOTHING) != 0)
				// ...set visibility to true
				e.setVisible(true);
			// if option 'blink' is set...
			if ((e.getOptions() & Emblem.BLINK) != 0)
				// ...negate the visibility of emblem.
				e.setVisible(!e.isVisible());
			// if option 'running' is set...
			if ((e.getOptions() & Emblem.RUNNING_RIGHT) != 0)
				// ...change the position of emblem.
				e.setPosx(e.getPosx() + 1);
			// if option 'running' is set...
			if ((e.getOptions() & Emblem.RUNNING_LEFT) != 0)
				// ...change the position of emblem.
				e.setPosx(e.getPosx() + widthMatrix - 1);

			buildMap();
			repaint();
		}
	}

	public int getWidthMatrix() {
		return widthMatrix;
	}

	public void setWidthMatrix(int width) {
		this.widthMatrix = width;
	}

	public int getHeightMatrix() {
		return heightMatrix;
	}

	public void setHeightMatrix(int height) {
		this.heightMatrix = height;
	}

	public Color getInactiveColor() {
		return inactiveColor;
	}

	public void setInactiveColor(Color inactiveColor) {
		this.inactiveColor = inactiveColor;
	}

	public Color getActiveColor() {
		return activeColor;
	}

	public void setActiveColor(Color activeColor) {
		this.activeColor = activeColor;
	}

	public Color getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean isDrawBorder() {
		return drawBorder;
	}

	public void setDrawBorder(boolean drawBorder) {
		this.drawBorder = drawBorder;
	}

	public boolean isDrawShadow() {
		return drawShadow;
	}

	public void setDrawShadow(boolean drawShadow) {
		this.drawShadow = drawShadow;
	}

}

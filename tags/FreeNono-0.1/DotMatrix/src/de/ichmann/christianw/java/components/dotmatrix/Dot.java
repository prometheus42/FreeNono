package de.ichmann.christianw.java.components.dotmatrix;

import java.awt.geom.*;

/**
 * Representation of one Dot in a DotMatrix component.
 * 
 * @author Christian Wichmann
 * @version 0.2
 */
public class Dot extends Ellipse2D.Double { // RoundRectangle2D.Double???

	private static final long serialVersionUID = 1L;

	private boolean active;

	public Dot(int x, int y, int d, boolean isActive) {
		super(x, y, d, d);
		active = isActive;
	}

	public Dot(int x, int y, int d) {
		this(x, y, d, false);
	}

	public Dot() {
		this(0, 0, 0, false);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setDiameter(double d) {
		this.height = this.width = d;
	}

}

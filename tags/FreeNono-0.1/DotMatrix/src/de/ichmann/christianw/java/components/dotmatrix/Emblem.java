package de.ichmann.christianw.java.components.dotmatrix;

/**
 * Represents an emblem, that is a text or symbol or a combination including
 * their position on the DotMatrix and all options and properties regarding
 * effects!
 * 
 * @author Christian Wichmann
 * @version 0.1
 */
public class Emblem {
	private String text;
	// current position of emblem (incl effect options)
	private int positionX, positionY;
	// destination position of emblem (after completion of effect)
	private final int destinationX, destinationY;
	private long options;
	private boolean visible = true;
	private int duration = 100;
	public final static long NOTHING = 0;
	public final static long BLINK = 1;
	public final static long RUNNING_LEFT = 2;
	public final static long RUNNING_RIGHT = 4;
	public final static long RAINBOW = 8;

	public Emblem() {
		this("", 0, 0);
	}

	public Emblem(String text, int posx, int posy) {
		this(text, posx, posy, NOTHING);
	}

	public Emblem(String text, int posx, int posy, long options) {
		this.text = text;
		this.destinationX = posx;
		this.positionX = this.destinationX;
		this.destinationY = posy;
		this.positionY = this.destinationY;
		this.options = options;
	}

	@Override
	public String toString() {
		return text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPosx() {
		return positionX;
	}

	public void setPosx(int posx) {
		this.positionX = posx;
	}

	public int getPosy() {
		return positionY;
	}

	public void setPosy(int posy) {
		this.positionY = posy;
	}

	public void setOptions(long options) {
		this.options = options;
	}

	public long getOptions() {
		return options;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

}

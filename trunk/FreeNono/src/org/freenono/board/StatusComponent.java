package org.freenono.board;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.freenono.event.GameAdapter;
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Game;

import de.ichmann.christianw.java.components.dotmatrix.DotMatrix;
import de.ichmann.christianw.java.components.dotmatrix.Emblem;

public class StatusComponent extends JPanel {

	private static final long serialVersionUID = 1283871798919081849L;

	private Game game;
	private GameEventHelper eventHelper;

	private DotMatrix displayTime;
	private Emblem remainingTime;

	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private JLabel jlabel;

	private final SimpleDateFormat timeFormatter = new SimpleDateFormat("mm:ss");
	private String timeLeft = "00:00";
	private int failCountLeft;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void Timer(GameEvent e) {
			refreshTime();
		}

		public void WrongFieldOccupied(GameEvent e) {
			refreshFailCount();
		}

	};

	public StatusComponent(Game game) {
		this.game = game;

		// set layout
		c.gridheight = 3;
		c.gridwidth = 0;
		c.insets = new Insets(10, 0, 10, 0);
		this.setLayout(layout);

		// set border
		Border border = new EtchedBorder(EtchedBorder.RAISED);
		this.setBorder(border);

		// format and display time
		if (game.usesMaxTime()) {
			timeLeft = timeFormatter.format(game.getTimeLeft());
		} else {
			timeLeft = timeFormatter.format(game.getElapsedTime());
		}
		remainingTime = new Emblem(timeLeft, 1, 0);
		displayTime = new DotMatrix(36, 8);
		displayTime.addEmblem(remainingTime);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		this.add(displayTime, c);

		// set fail count label
		jlabel = new JLabel();
		jlabel.setFont(new Font("FreeSans", Font.PLAIN, 18));
		failCountLeft = game.getFailCountLeft();
		if (failCountLeft != 0) {
			jlabel.setText(Integer.toString(failCountLeft) + " errors left");
		}
		c.gridy = GridBagConstraints.RELATIVE;
		this.add(jlabel);

	}

	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}

	private void refreshTime() {
		if (game.usesMaxTime()) {
			timeLeft = timeFormatter.format(game.getTimeLeft());
		} else {
			timeLeft = timeFormatter.format(game.getElapsedTime());
		}
		remainingTime.setText(timeLeft);
		displayTime.refresh();
	}

	private void refreshFailCount() {

		failCountLeft = game.getFailCountLeft();

		if (failCountLeft != 0) {
			jlabel.setText(Integer.toString(failCountLeft) + " errors left");
		}

	}

	public void addPreviewArea(BoardPreview previewArea) {
		c.gridy = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.PAGE_END;
		this.add(previewArea, c);
	}

	/**
	 * paints an gradient over the statusComponent (source by:
	 * http://weblogs.java.net/blog/gfx/archive/2006/09/java2d_gradient.html)
	 * 
	 */
	// protected void paintComponent(Graphics g) {
	// Graphics2D g2 = (Graphics2D) g;
	// BufferedImage cache = null;
	// if (cache == null || cache.getHeight() != getHeight()) {
	// cache = new BufferedImage(2, getHeight(),
	// BufferedImage.TYPE_INT_RGB);
	// Graphics2D g2d = cache.createGraphics();
	//
	// GradientPaint paint = new GradientPaint(0, 0, Color.WHITE, 0,
	// getHeight(), Color.GRAY);
	// g2d.setPaint(paint);
	// g2d.fillRect(0, 0, 2, getHeight());
	// g2d.dispose();
	// }
	// g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
	// }

}

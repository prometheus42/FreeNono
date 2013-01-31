/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *****************************************************************************/
package org.freenono.board;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.GameModeType;
import org.freenono.model.GameTime;
import org.freenono.ui.Messages;

public class StatusComponent extends JPanel {

	private static final long serialVersionUID = 1283871798919081849L;

	private static Logger logger = Logger.getLogger(StatusComponent.class);

	protected GameEventHelper eventHelper = null;
	protected Settings settings = null;

	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private JLabel failCountDisplay;
	private JLabel timeDisplay;

	private int failCountLeft = 0;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void SetFailCount(StateChangeEvent e) {
			if (settings.getGameMode() == GameModeType.MAX_FAIL) {
				refreshFailCount(e.getFailCount());
			}
		}

		@Override
		public void Timer(StateChangeEvent e) {
			refreshTime(e.getGameTime());
		}

		@Override
		public void SetTime(StateChangeEvent e) {
			refreshTime(e.getGameTime());
		}

		@Override
		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				break;

			case solved:
				break;

			case paused:
				break;

			case running:
				break;

			default:
				break;
			}

		}

	};

	
	public StatusComponent(Settings settings) {

		this.settings = settings;
		
		initialize();
	}

	
	private void initialize() {
		
		// TODO show fixed-sized StatusComponent if used or not!
		//this.setSize(100, 100);
		
		// set layout
		c.gridheight = 3;
		c.gridwidth = 0;
		c.insets = new Insets(10, 0, 10, 0);
		this.setLayout(layout);

		// set border
		Border border = new EtchedBorder(EtchedBorder.RAISED);
		this.setBorder(border);

		// add time to component
		timeDisplay = new JLabel();
		timeDisplay.setFont(new Font("LCDMono2", Font.PLAIN, 36)); //$NON-NLS-1$
		timeDisplay.setForeground(new Color(110, 95, 154));
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		this.add(timeDisplay, c);

		// set fail count label
		if (settings.getGameMode() == GameModeType.MAX_FAIL) {
			failCountDisplay = new JLabel();
			failCountDisplay.setFont(new Font("FreeSans", Font.PLAIN, 18)); //$NON-NLS-1$
			refreshFailCount(settings.getMaxFailCount());
			c.gridy = GridBagConstraints.RELATIVE;
			c.anchor = GridBagConstraints.CENTER;
			this.add(failCountDisplay);
		}
	}

	public void setEventHelper(GameEventHelper eventHelper) {

		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}
	
	public void removeEventHelper() {
		
		eventHelper.removeGameListener(gameAdapter);
		this.eventHelper = null;
	}

	private void refreshTime(GameTime gameTime) {

		timeDisplay.setText(gameTime.toString());
	}

	private void refreshFailCount(int failCount) {

		failCountLeft = failCount;

		if (failCountLeft != 0) {
			failCountDisplay.setText(Integer.toString(failCountLeft)
					+ Messages.getString("StatusComponent.ErrorsLeft")); //$NON-NLS-1$
		}
	}

	public void addPreviewArea(BoardPreview previewArea) {

		c.gridy = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.SOUTH;
		this.add(previewArea, c);
	}

	/**
	 * paints an gradient over the statusComponent (source by:
	 * http://weblogs.java.net/blog/gfx/archive/2006/09/java2d_gradient.html)
	 * 
	 */
	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		BufferedImage cache = null;
		if (cache == null || cache.getHeight() != getHeight()) {
			cache = new BufferedImage(2, getHeight(),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = cache.createGraphics();

			GradientPaint paint = new GradientPaint(0, 0, Color.WHITE, 0,
					getHeight(), new Color(231, 224, 143));
			g2d.setPaint(paint);
			g2d.fillRect(0, 0, 2, getHeight());
			g2d.dispose();
		}
		g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
	}

}

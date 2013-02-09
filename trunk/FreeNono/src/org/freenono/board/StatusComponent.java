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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

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

	private GridBagLayout layout;
	private GridBagConstraints constraints;
	private JLabel failCountLabel;
	private JLabel failCountDisplay;
	private JLabel timeLabel;
	private JLabel timeDisplay;
	private JLabel gameModeDisplay;
	private JLabel gameModeLabel;

	private Font fontLCD = null;
	private Font fontText = null;

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

		loadFonts();

		initialize();
	}

	private void loadFonts() {

		fontLCD = new Font("LCDMono2", Font.PLAIN, 36); //$NON-NLS-1$
		fontText = new Font("FreeSans", Font.PLAIN, 18); //$NON-NLS-1$
	}

	private void initialize() {

		// set GridBagLayout as layout manager
		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		this.setLayout(layout);
		constraints.insets = new Insets(15, 15, 15, 15);

		// set border
		Border border = new EtchedBorder(EtchedBorder.RAISED);
		this.setBorder(border);

		// add game mode description
		gameModeLabel = new JLabel(
				Messages.getString("StatusComponent.GameModeLabel"));
		gameModeLabel.setFont(fontText);
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 0;
		this.add(gameModeLabel, constraints);

		gameModeDisplay = new JLabel(settings.getGameMode().toString());
		gameModeDisplay.setFont(fontLCD);
		gameModeDisplay.setForeground(new Color(110, 95, 154));
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		constraints.gridx = 2;
		constraints.gridy = 1;
		this.add(gameModeDisplay, constraints);

		// add time to component
		if (settings.getGameMode() == GameModeType.COUNT_TIME
				|| settings.getGameMode() == GameModeType.MAX_TIME
				|| settings.getGameMode() == GameModeType.PENALTY) {

			timeLabel = new JLabel(
					Messages.getString("StatusComponent.TimeLabel"));
			timeLabel.setFont(fontText);
			constraints.gridheight = 1;
			constraints.gridwidth = 2;
			constraints.gridx = 0;
			constraints.gridy = 2;
			this.add(timeLabel, constraints);

			timeDisplay = new JLabel();
			timeDisplay.setFont(fontLCD);
			timeDisplay.setForeground(new Color(110, 95, 154));
			constraints.gridheight = 1;
			constraints.gridwidth = 2;
			constraints.gridx = 2;
			constraints.gridy = 3;
			this.add(timeDisplay, constraints);
		}

		// set fail count label
		else if (settings.getGameMode() == GameModeType.MAX_FAIL) {

			failCountLabel = new JLabel(
					Messages.getString("StatusComponent.FailCountLabel"));
			failCountLabel.setFont(fontText);
			constraints.gridheight = 1;
			constraints.gridwidth = 2;
			constraints.gridx = 0;
			constraints.gridy = 2;
			this.add(failCountLabel, constraints);

			failCountDisplay = new JLabel();
			failCountDisplay.setFont(fontText);
			refreshFailCount(settings.getMaxFailCount());
			constraints.gridheight = 1;
			constraints.gridwidth = 2;
			constraints.gridx = 2;
			constraints.gridy = 3;
			this.add(failCountDisplay, constraints);
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

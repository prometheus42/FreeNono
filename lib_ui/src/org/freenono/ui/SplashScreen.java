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
package org.freenono.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

public class SplashScreen extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final int TIMER_DELAY = 3000;
	private final Timer timer = new Timer();
	private Image image = null;
	private Integer timerDelay = TIMER_DELAY;

	/**
	 * This is the default constructor
	 */
	public SplashScreen(int timerDelay) {
		
		super();
		
		this.timerDelay = timerDelay;
		
		initialize();
		
		if (timerDelay != 0)
			setupTimer();

		image = new ImageIcon(getClass()
				.getResource("/resources/icon/splashscreen.png")).getImage();
	}

	public SplashScreen(String ressource) {
		
		super();
		
		initialize();
		
		setupTimer();
			
		image = new ImageIcon(getClass().getResource(ressource)).getImage();

	}
	
	private void setupTimer() {
		
		timer.schedule(new TimerTask() {
			public void run() {
				close();
			}
		}, TIMER_DELAY);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.setSize(765, 505);
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.setTitle("");
		this.addKeyListener(new java.awt.event.KeyAdapter() {
			
			@Override
			public void keyPressed(java.awt.event.KeyEvent e) {
				close();
			}
			
			@Override
			public void keyTyped(java.awt.event.KeyEvent e) {
				close();
			}
			
			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				close();
			}
		});
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				close();
			}
			
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				close();
			}
			
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				close();
			}
		});
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
	}

	private void close() {
		
		setVisible(false);
		dispose();
	}
	
	@Override
	public void paint(Graphics g) {
		
		super.paintComponents(g);

		g.drawImage(
				image,
				0,
				0,
				this.getWidth(),
				this.getHeight(),
				this);
	}

}

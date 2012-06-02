/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;


public class AboutDialog2 extends JDialog implements Runnable {

	private static final long serialVersionUID = -3174417107818960578L;
	
	private static Logger logger = Logger.getLogger(AboutDialog.class);

	private JScrollPane scroll = null;
	private XHTMLPanel panel = null;
	
	private String title = null;
	private String filename = null;
	
	
	public AboutDialog2(String title, String filename) {

		super();

		this.title = title;
		this.filename = filename;

		initialize();

		addListener();
	}
	
	
	private void initialize() {

		int w = 400, h = 600;

		this.setTitle(title);
		this.setUndecorated(true);

		// set up XHTML panel
		panel = new XHTMLPanel();
		panel.setPreferredSize(new Dimension(w, h));

		// add scroll pane to this dialog
		scroll = new FSScrollPane(panel);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(w, h));
		getContentPane().add(scroll);

		try {
			panel.setDocument(new File(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set size and position in middle of screen
		this.pack();
		this.setSize(w, h);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screen.width - w) / 2, (screen.height - h) / 2);
		this.setVisible(true);
	}
	
	private void addListener() {
		
		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				performExit();
			}
		});
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				performExit();
			}
		});
	}
	
	private void performExit() {
		
		this.dispose();
	}

	
	@Override
	public void run() {

		while (true) {
			
			try {
				Thread.sleep(100);

			} catch (InterruptedException e) {

				logger.debug("thread interrupted!");
			}

			JScrollBar sb = scroll.getVerticalScrollBar();
			sb.setValue(sb.getValue() + 10);
		}
	}
	
	public static void main(String[] args) {
        
		AboutDialog2 ab = new AboutDialog2("About", "/home/christian/Java/FreeNono/docs/manual/index.html");
	}

}

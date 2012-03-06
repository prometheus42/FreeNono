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

import javax.swing.JPanel;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.WindowConstants;

public class AboutUI extends JDialog {

	private static final long serialVersionUID = -78784201445320344L;

	private static Logger logger = Logger.getLogger("de.ichmann.markusw.java.apps.nonogram");  //  @jve:decl-index=0:
	
	private JPanel jContentPane = null;
	
	private JEditorPane jPane = null;
	
	/**
	 * @param owner
	 */
	public AboutUI(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 400);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			BorderLayout borderLayout = new BorderLayout();
			jContentPane.setLayout(borderLayout);
			jContentPane.add(getJPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	private JEditorPane getJPane() {
		if (jPane == null) {
			jPane = new JEditorPane();
			jPane.setEditable(false);
			
			/* close About box when mouse is clicked or key is pressed */
			jPane.addKeyListener(new java.awt.event.KeyAdapter() {
				
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
			jPane.addMouseListener(new java.awt.event.MouseAdapter() {
				
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
					
			/* insert content for editor pane */
			String type = "text/html";
			jPane.setContentType(type);
			jPane.setEditorKit(jPane.getEditorKitForContentType(type));
			String content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
			content += "<html><body style=\"background-color:#7C7C88;font-family:sans-serif;\">";
			content += "<h1 style=\"letter-spacing: 0.25em;text-shadow: #AAA 2px 2px 2px;border-bottom: 1px solid black;padding: 0 0 0.25em 0;\">FreeNono::About</h1>";
			content += "<dl style=\"font-family:monospace;color:black;margin:10em;border:2px solid #8B77C3;\">";
			content += "<dt>Lead Programmer</dt><dd>Markus Wichmann</dd>";
			content += "<dt>Graphical Concept</dt><dd>Christian Wichmann</dd>";
			content += "<dt>Musical Coordinator</dt><dd>Martin Wichmann</dd>";
			content += "</dl></body></html>";
			jPane.setText(content);
		}
		return jPane;
	}

	private void close() {
		setVisible(false);
		dispose();
	}
	
}

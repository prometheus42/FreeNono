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
package de.ichmann.markusw.java.apps.nonogram.ui;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JButton;

public class AboutUI extends JDialog {

	private static final long serialVersionUID = -78784201445320344L;

	private static Logger logger = Logger.getLogger("de.ichmann.markusw.java.apps.nonogram");  //  @jve:decl-index=0:
	
	private JPanel jContentPane = null;

	private JTextArea jTextArea = null;

	private JButton jButton = null;

	private JPanel jPanel1 = null;

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
		this.setSize(300, 200);
		this.setModal(true);
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
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel1(), BorderLayout.CENTER);
			jContentPane.add(getJButton(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			
			jTextArea.setText(
					"Nonogram\n"+
					"\n"+
					"Version: 0.0.1\n" +
					"Build id: 20101208-0001\n" +
					"\n" +
					"Published under terms of the GPL\n" +
					"Visit http://......\n");
		}
		return jTextArea;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Close");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(0);
			jPanel1 = new JPanel();
			jPanel1.setLayout(borderLayout);
			jPanel1.add(getJTextArea(), BorderLayout.CENTER);
		}
		return jPanel1;
	}

}

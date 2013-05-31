/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Martin Wichmann, Christian Wichmann
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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.freenono.controller.ControlSettings;
import org.freenono.controller.ControlSettings.Control;


public class NewKeyAssignmentDialog extends JDialog {

	private static final long serialVersionUID = 8423411694004619728L;
		
	private static Logger logger = Logger.getLogger(NewKeyAssignmentDialog.class);

	private JLabel warningText;
	
	private int newKeyCode;
	
	private ControlSettings cs;
	private Control c;

	
	public NewKeyAssignmentDialog(ControlSettings cs, Control c) {

		this.cs = cs;
		this.c = c;
		
		newKeyCode = cs.getControl(c);

		initialize();

		addListener();
		
		setVisible(true);
	}

	
	private void initialize() {
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle(Messages.getString("OptionsUI.UserKeyPromptTitle"));
		
		// set layout manager
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(20, 20, 20, 20);
		
		// add labels for user message and warning
		JLabel hint = new JLabel(Messages.getString("OptionsUI.UserKeyPrompt"), JLabel.CENTER);
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(hint, c);
		
		warningText = new JLabel("", JLabel.CENTER);
		warningText.setForeground(Color.RED);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(warningText, c);
		
		pack();
	}

	private void addListener() {

		addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (!keycodeAlreadyUsed(e.getKeyCode())) {

					setNewKeyCode(e.getKeyCode());
					logger.debug("New key code for control " + c + ": "
							+ e.getKeyCode());
					
					dispose();
				}
				
				warningText.setText(Messages.getString("OptionsUI.WarningAssignKey"));
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
	}

	
	private boolean keycodeAlreadyUsed(int enteredKeyCode) {
		
		if (enteredKeyCode != newKeyCode) {
			
			if (enteredKeyCode == cs.getControl(Control.moveLeft))
				return true;
			if (enteredKeyCode == cs.getControl(Control.moveRight))
				return true;
			if (enteredKeyCode == cs.getControl(Control.moveUp))
				return true;
			if (enteredKeyCode == cs.getControl(Control.moveDown))
				return true;
			if (enteredKeyCode == cs.getControl(Control.markField))
				return true;
			if (enteredKeyCode == cs.getControl(Control.occupyField))
				return true;
		}

		return false;
	}


	public int getNewKeyCode() {
		
		return newKeyCode;
	}


	private void setNewKeyCode(int newKeyCode) {
		
		this.newKeyCode = newKeyCode;
	}

}

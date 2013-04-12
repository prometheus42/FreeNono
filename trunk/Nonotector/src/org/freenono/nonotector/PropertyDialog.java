/*****************************************************************************
 * Nonotector - Detector to import nonograms from scanned images
 * Copyright (c) 2013 Christian Wichmann
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
package org.freenono.nonotector;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.freenono.model.DifficultyLevel;


public class PropertyDialog extends JDialog {

	private static final long serialVersionUID = -4301622186488894087L;
	
	private JPanel propertyPanel;
	private JLabel difficultyLabel;
	private JComboBox difficultyComboBox;
	private JLabel creatorLabel;
	private JTextField creatorField;
	private JPanel buttonPanel;

	
	public PropertyDialog(JFrame parent) {
		
		super(parent, true);
		
		initialize();
	}

	private void initialize() {
		
		setTitle("Property Dialog");
		setModalityType(DEFAULT_MODALITY_TYPE);
		setSize(600, 600);
		
		setLayout(new BorderLayout());
		add(getPropertyPanel(), BorderLayout.NORTH);
		add(getButtonPanel(), BorderLayout.SOUTH);
		
		pack();

		// handle window closing correctly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				
			}
		});
	}

	private JPanel getPropertyPanel() {
		
		propertyPanel = new JPanel();
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints(); 
		propertyPanel.setLayout(gbl);
		
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;
		creatorLabel = new JLabel("Creator: ");
		propertyPanel.add(creatorLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 0;
		creatorField = new JTextField(25);
		creatorField.setText(NonogramStore.getCreator());
		propertyPanel.add(creatorField, gc);
		
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 1;
		difficultyLabel = new JLabel("Difficulty: ");
		propertyPanel.add(difficultyLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 1;
		difficultyComboBox = new JComboBox(DifficultyLevel.values());
		difficultyComboBox.setSelectedItem(NonogramStore.getDifficulty());
		propertyPanel.add(difficultyComboBox, gc);
		
		return propertyPanel;
	}
	
	private JPanel getButtonPanel() {
		
		buttonPanel = new JPanel();
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				performOK();
			}
		});
		buttonPanel.add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performCancel();
			}
		});
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private void performCancel() {
		
		setVisible(false);
	}

	private void performOK() {
		
		NonogramStore.setCreator(creatorField.getText());
		
		NonogramStore.setDifficulty((DifficultyLevel) difficultyComboBox.getSelectedItem());
		
		setVisible(false);
	}

}

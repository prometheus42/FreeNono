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
import java.awt.Insets;
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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.freenono.model.DifficultyLevel;


public class PropertyDialog extends JDialog {

	private static final long serialVersionUID = -4301622186488894087L;
	
	private int lastClicked = 0;
	public final int OK_BUTTON = 1;
	public final int CANCEL_BUTTON = 2;
	
	private JPanel propertyPanel;
	private JLabel difficultyLabel;
	private JComboBox difficultyComboBox;
	private JLabel creatorLabel;
	private JTextField creatorField;
	private JLabel descriptionLabel;
	private JTextArea descriptionField;
	private JPanel buttonPanel;
	private JLabel levelLabel;
	private JSpinner levelSpinner;
	private JLabel nameLabel;
	private JTextField nameField;
	private JLabel widthLabel;
	private JSpinner widthSpinner;
	private JLabel heightLabel;
	private JSpinner heightSpinner;
	
	
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
		gc.insets = new Insets(15, 15, 15, 15);
		
		/***** creator *****/
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;
		creatorLabel = new JLabel("Creator: ");
		propertyPanel.add(creatorLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 0;
		creatorField = new JTextField(15);
		creatorField.setText(NonogramStore.getCreator());
		propertyPanel.add(creatorField, gc);
		
		/***** name *****/
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 1;
		nameLabel = new JLabel("Name: ");
		propertyPanel.add(nameLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 1;
		nameField = new JTextField(15);
		nameField.setText(NonogramStore.getName());
		propertyPanel.add(nameField, gc);
		
		/***** description *****/
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 2;
		descriptionLabel = new JLabel("Description: ");
		propertyPanel.add(descriptionLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 2;
		descriptionField = new JTextArea(5, 20);
		descriptionField.setText(NonogramStore.getDescription());
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
		propertyPanel.add(descriptionField, gc);
		
		/***** difficulty *****/
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 3;
		difficultyLabel = new JLabel("Difficulty: ");
		propertyPanel.add(difficultyLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 3;
		difficultyComboBox = new JComboBox(DifficultyLevel.values());
		difficultyComboBox.setSelectedItem(NonogramStore.getDifficulty());
		propertyPanel.add(difficultyComboBox, gc);
		
		/***** level *****/
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 4;
		levelLabel = new JLabel("Level: ");
		propertyPanel.add(levelLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 4;
		SpinnerNumberModel levelSpinnerModel = new SpinnerNumberModel(
				(int) NonogramStore.getLevel(), 0, 200, 1);
		// SpinnerNumberModel(value, min, max, step)
		levelSpinner = new JSpinner(levelSpinnerModel);
		propertyPanel.add(levelSpinner, gc);
		
		/***** width *****/
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 5;
		widthLabel = new JLabel("Width: ");
		propertyPanel.add(widthLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 5;
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel(
				(int) NonogramStore.getWidth(), 0, 50, 1);
		widthSpinner = new JSpinner(widthSpinnerModel);
		propertyPanel.add(widthSpinner, gc);
		
		/***** height *****/
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 6;
		heightLabel = new JLabel("Height: ");
		propertyPanel.add(heightLabel, gc);
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 1;
		gc.gridy = 6;
		SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel(
				(int) NonogramStore.getHeight(), 0, 200, 1);
		// SpinnerNumberModel(value, min, max, step)
		heightSpinner = new JSpinner(heightSpinnerModel);
		propertyPanel.add(heightSpinner, gc);
		
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
		
		setLastClicked(CANCEL_BUTTON);
		setVisible(false);
	}

	private void performOK() {
		
		NonogramStore.setCreator(creatorField.getText());
		NonogramStore.setName(nameField.getText());
		NonogramStore.setLevel((Integer)levelSpinner.getValue());
		NonogramStore.setDifficulty((DifficultyLevel) difficultyComboBox.getSelectedItem());
		NonogramStore.setLevel((Integer)levelSpinner.getValue());
		NonogramStore.setWidth((Integer)widthSpinner.getValue());
		NonogramStore.setHeight((Integer)heightSpinner.getValue());
		NonogramStore.setDescription(descriptionField.getText());
		
		// there's always next time...
		nameField.setText("");
		nameField.requestFocusInWindow();
		levelSpinner.setValue((Integer)levelSpinner.getValue() + 1);
		
		setLastClicked(OK_BUTTON);
		setVisible(false);
	}

	public int getLastClicked() {
		
		return lastClicked;
	}

	private void setLastClicked(int lastClicked) {
		
		this.lastClicked = lastClicked;
	}

}

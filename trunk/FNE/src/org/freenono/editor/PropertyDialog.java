/*****************************************************************************
 * FreeNonoEditor - A editor for nonogram riddles
 * Copyright (c) 2012 Christian Wichmann
 * 
 * File name: $HeadURL$
 * Revision: $Revision$
 * Last modified: $Date$
 * Last modified by: $Author$
 * $Id$
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
package org.freenono.editor;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;

public class PropertyDialog extends JDialog implements PropertyChangeListener {

	private static Logger logger = Logger.getLogger(PropertyDialog.class);

	private static final long serialVersionUID = -1187678629156219735L;

	private JLabel nameLabel = null;
	private JTextField nameTextField = null;
	private JLabel authorLabel = null;
	private JTextField authorTextField = null;
	private JLabel levelLabel = null;
	private JSpinner levelSpinner = null;
	private JLabel descriptionLabel = null;
	private JTextArea descriptionTextField = null;
	private JLabel difficultyLabel = null;
	private JComboBox difficultyComboBox = null;
	private JLabel heightLabel = null;
	private JSlider sliderHeight = null;
	private JLabel widthLabel = null;
	private JSlider sliderWidth = null;

	private static final int SIZE_MIN = 0;
	private static final int SIZE_MAX = 40;
	private static final int SIZE_INIT = 15;

	private JOptionPane optionPane = null;
	private String okButtonString = Messages.getString("PropertyDialog.OKButton"); //$NON-NLS-1$
	private String cancelButtonString = Messages.getString("PropertyDialog.CancelButton"); //$NON-NLS-1$

	private Nonogram nonogram = null;


	public PropertyDialog(Frame parent) {

		super(parent, true);

		initialize();
		
		logger.debug("Opening property dialog...");
	}

	
	private void initialize() {

		// set general options for dialog
		setTitle(Messages.getString("PropertyDialog.PropertyEditorTitle")); //$NON-NLS-1$
		setModalityType(DEFAULT_MODALITY_TYPE); // TODO: check modality

		// create name and description option
		nameLabel = new JLabel(Messages.getString("PropertyDialog.PropertyName")); //$NON-NLS-1$
		nameTextField = new JTextField(20);
		descriptionLabel = new JLabel(Messages.getString("PropertyDialog.PropertyDescription")); //$NON-NLS-1$
		descriptionTextField = new JTextArea(4, 20);
		authorLabel = new JLabel(Messages.getString("PropertyDialog.PropertyAuthor")); //$NON-NLS-1$
		authorTextField = new JTextField(20);
		
		// create spinner for level attribute
		levelLabel = new JLabel(Messages.getString("PropertyDialog.PropertyLevel")); //$NON-NLS-1$
		SpinnerModel spinnerModel =
		        new SpinnerNumberModel(0, 		// initial value
		                               0, 		// min
		                               100,		// max
		                               1);		// step
		levelSpinner = new JSpinner(spinnerModel);

		// create difficulty option
		difficultyLabel = new JLabel(Messages.getString("PropertyDialog.PropertyDifficulty")); //$NON-NLS-1$
		difficultyComboBox = new JComboBox(DifficultyLevel.values());

		// create slider for size options
		heightLabel = new JLabel(Messages.getString("PropertyDialog.PropertyHeight")); //$NON-NLS-1$
		sliderHeight = new JSlider(JSlider.HORIZONTAL, SIZE_MIN, SIZE_MAX,
				SIZE_INIT);
		sliderHeight.setMajorTickSpacing(10);
		sliderHeight.setMinorTickSpacing(1);
		sliderHeight.setSnapToTicks(true);
		sliderHeight.setPaintTicks(true);
		sliderHeight.setPaintLabels(true);
		widthLabel = new JLabel(Messages.getString("PropertyDialog.PropertyWidth")); //$NON-NLS-1$
		sliderWidth = new JSlider(JSlider.HORIZONTAL, SIZE_MIN, SIZE_MAX,
				SIZE_INIT);
		sliderWidth.setMajorTickSpacing(10);
		sliderWidth.setMinorTickSpacing(1);
		sliderWidth.setSnapToTicks(true);
		sliderWidth.setPaintTicks(true);
		sliderWidth.setPaintLabels(true);

		// generate object list for option pane
		Object[] array = { nameLabel, nameTextField, descriptionLabel,
				descriptionTextField, authorLabel, authorTextField, levelLabel,
				levelSpinner, difficultyLabel, difficultyComboBox, heightLabel,
				sliderHeight, widthLabel, sliderWidth };
		Object[] options = { okButtonString, cancelButtonString };

		// create option pane
		optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options, options[0]);

		// make this dialog display it
		setContentPane(optionPane);

		this.pack();

		// handle window closing correctly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window, we're going to change
				 * the JOptionPane's value property.
				 */
				optionPane.setValue(new Integer(JOptionPane.CANCEL_OPTION));//CLOSED_OPTION
			}
		});

		// register an event handler that reacts to option pane state changes
		optionPane.addPropertyChangeListener(this);
	}


	/**
	 * This method reacts to state changes in the option pane 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {

		String prop = e.getPropertyName();

		if (isVisible()
				&& (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
						.equals(prop))) {
			Object value = optionPane.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				return;
			}

			// Reset the JOptionPane's value.
			// If you don't do this, then if the user presses the same button
			// next time, no property change event will be fired.
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (okButtonString.equals(value)) {
		
				if (nameTextField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(PropertyDialog.this,
							Messages.getString("PropertyDialog.ErrorInvalidName"), Messages.getString("PropertyDialog.ErrorInvalidNameTitle"), //$NON-NLS-1$ //$NON-NLS-2$
							JOptionPane.ERROR_MESSAGE);
					nameTextField.requestFocusInWindow();

				// } else if (descriptionTextField.getText().isEmpty()) {
				// JOptionPane.showMessageDialog(PropertyDialog.this,
				// "Invalid nonogram description!", "Try again",
				// JOptionPane.ERROR_MESSAGE);
				// descriptionTextField.requestFocusInWindow();
					
				} else {
					saveChanges();
					clearAndHide();
				}

			} else {
				clearAndHide();
			}
		}
	}

	private void saveChanges() {

		if (nonogram == null) {
			nonogram = new Nonogram(
					nameTextField.getText(),
					(DifficultyLevel) difficultyComboBox.getSelectedItem(),
					new boolean[sliderHeight.getValue()][sliderWidth.getValue()]);
			
		}

		nonogram.setName(nameTextField.getText());
		nonogram.setAuthor(authorTextField.getText());
		nonogram.setLevel((Integer) levelSpinner.getValue());
		nonogram.setDescription(descriptionTextField.getText());
		nonogram.setDifficulty((DifficultyLevel) difficultyComboBox
				.getSelectedItem());

		if (nonogram.width() != sliderWidth.getValue()
				|| nonogram.height() != sliderHeight.getValue()) {
			int answer = JOptionPane
					.showConfirmDialog(
							this,
							Messages.getString("PropertyDialog.QuestionSizeChange") //$NON-NLS-1$
									+ Messages.getString("PropertyDialog.QuestionSizeChange2"), //$NON-NLS-1$
							Messages.getString("PropertyDialog.QuestionSizeChangeTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

			if (answer == JOptionPane.OK_OPTION) {

				nonogram.setSize(sliderWidth.getValue(),
						sliderHeight.getValue());

			}
		}
	}

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {

		//nameTextField.setText(null);
		setVisible(false);
		
	}

	/**
	 * @return the nonogram
	 */
	public Nonogram getNonogram() {
		
		return nonogram;
		
	}

	/**
	 * @param nonogram
	 *            the nonogram to set
	 */
	public void setNonogram(Nonogram nonogram) {

		this.nonogram = nonogram;

		// set values for UI components
		nameTextField.setText(nonogram.getName());
		descriptionTextField.setText(nonogram.getDescription());
		authorTextField.setText(nonogram.getAuthor());
		levelSpinner.setValue(nonogram.getLevel());
		difficultyComboBox.setSelectedItem(nonogram.getDifficulty());
		sliderHeight.setValue(nonogram.height());
		sliderWidth.setValue(nonogram.width());
		
	}

}

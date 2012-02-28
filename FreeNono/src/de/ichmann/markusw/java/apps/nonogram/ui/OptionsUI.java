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
import javax.swing.JDialog;

import de.ichmann.markusw.java.apps.nonogram.model.Settings;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerDateModel;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OptionsUI extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private Settings settings;
	private JLabel lblMaxTime = null;
	private JSpinner maxTime = null;
	private JLabel lblMaxFailCount = null;
	private JSpinner maxFailCount = null;
	private JLabel lblMarkInvalid = null;
	private JLabel lblCountMarked = null;
	private JCheckBox markInvalid = null;
	private JCheckBox countMarked = null;
	private JButton btnOK = null;
	private JButton btnCancel = null;
	private JPanel jPanel = null;

	/**
	 * @param owner
	 */
	public OptionsUI(Frame owner, Settings settings) {
		super(owner);
		initialize();
		
		this.settings = settings;
		loadSettings();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(369, 176);
		this.setLocationRelativeTo(null);
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
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(5);
			borderLayout.setVgap(5);
			lblCountMarked = new JLabel();
			lblCountMarked.setText("Count marked fields");
			lblMarkInvalid = new JLabel();
			lblMarkInvalid.setText("Mark fields on invalid move");
			lblMaxFailCount = new JLabel();
			lblMaxFailCount.setText("Max fail count");
			lblMaxTime = new JLabel();
			lblMaxTime.setText("Max time");
			jContentPane = new JPanel();
			jContentPane.setLayout(borderLayout);
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes maxTime	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	private JSpinner getMaxTime() {
		if (maxTime == null) {
			SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
			spinnerDateModel.setCalendarField(Calendar.MINUTE);
			maxTime = new JSpinner();
			maxTime.setUI(new BasicSpinnerUI());
			maxTime.setModel(spinnerDateModel);
			maxTime.setEditor(new JSpinner.DateEditor(maxTime, "mm:ss"));
		}
		return maxTime;
	}

	/**
	 * This method initializes maxFailCount	
	 * 	
	 * @return javax.swing.JSpinner	
	 */
	private JSpinner getMaxFailCount() {
		if (maxFailCount == null) {
			maxFailCount = new JSpinner();
			maxFailCount.setUI(new BasicSpinnerUI());
			maxFailCount.setModel(new SpinnerNumberModel());
		}
		return maxFailCount;
	}

	/**
	 * This method initializes markInvalid	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getMarkInvalid() {
		if (markInvalid == null) {
			markInvalid = new JCheckBox();
		}
		return markInvalid;
	}

	/**
	 * This method initializes countMarked	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCountMarked() {
		if (countMarked == null) {
			countMarked = new JCheckBox();
		}
		return countMarked;
	}

	/**
	 * This method initializes btnOK	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnOK() {
		if (btnOK == null) {
			btnOK = new JButton();
			btnOK.setText("OK");
			btnOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveSettings();
					close();
				}
			});
		}
		return btnOK;
	}

	/**
	 * This method initializes btnCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new JButton();
			btnCancel.setText("Cancel");
			btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					close();
				}
			});
		}
		return btnCancel;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(5);
			gridLayout1.setHgap(5);
			gridLayout1.setVgap(5);
			gridLayout1.setColumns(2);
			jPanel = new JPanel();
			jPanel.setLayout(gridLayout1);
			jPanel.add(lblMaxFailCount, null);
			jPanel.add(getMaxFailCount(), null);
			jPanel.add(lblMaxTime, null);
			jPanel.add(getMaxTime(), null);
			jPanel.add(lblMarkInvalid, null);
			jPanel.add(getMarkInvalid(), null);
			jPanel.add(lblCountMarked, null);
			jPanel.add(getCountMarked(), null);
			jPanel.add(getBtnCancel(), null);
			jPanel.add(getBtnOK(), null);
		}
		return jPanel;
	}
	
	private void loadSettings() {
		
		maxFailCount.setValue(this.settings.getMaxFailCount());
		maxTime.setValue(new Date(this.settings.getMaxTime()));
		markInvalid.setSelected(this.settings.getMarkInvalid());
		countMarked.setSelected(this.settings.getCountMarked());
	}
	
	private void saveSettings() {
		
		Integer i = (Integer)maxFailCount.getValue();
		settings.setMaxFailCount(i.intValue());
		
		Date d = (Date)maxTime.getValue();
		Calendar c = Calendar.getInstance();
		settings.setMaxTime(d.getTime() + (c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)));
		
		settings.setMarkInvalid(markInvalid.isSelected());
		
		settings.setCountMarked(countMarked.isSelected());
	}

	private void close() {
		this.setVisible(false);
		this.dispose();
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"

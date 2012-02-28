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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTree;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;

import org.freenono.model.Nonogram;
import org.freenono.model.RandomNonogram.RandomTypes;

public class NonogramChooserUI extends JDialog {

	private static final long serialVersionUID = -6359071603972829942L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtNotYetImplemented;
	private JComboBox comboBox;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JRadioButton rdbtnPredefinedNonograms;
	private JRadioButton rdbtnRandomNonogram;
	private JRadioButton rdbtnNonogramBySeed;
	private JScrollPane scrollPane;
	private JSlider sliderHeight;
	private JSlider sliderWidth;
	private JLabel labelHeight;
	private JLabel labelWidth;
	private JButton okButton;

	private boolean validOptions = false;
	private int type = 0;
	private Nonogram choosenNono = null;
	private RandomTypes randomType = null;
	private String seed = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			NonogramChooserUI dialog = new NonogramChooserUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public NonogramChooserUI() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		//setLocationRelativeTo(null);
		setBounds(50, 50, 315, 475);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		txtNotYetImplemented = new JTextField();
		txtNotYetImplemented.setText(Messages
				.getString("NonogramChooserUI.NotYetImplemented"));
		txtNotYetImplemented.setEnabled(false);
		txtNotYetImplemented.setBounds(68, 387, 221, 19);
		contentPanel.add(txtNotYetImplemented);
		txtNotYetImplemented.setColumns(10);

		contentPanel.setLayout(null);

		JLabel lblSeed = new JLabel(
				Messages.getString("NonogramChooserUI.SeedLabel"));
		lblSeed.setBounds(18, 389, 52, 15);
		contentPanel.add(lblSeed);

		comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setEditable(false);
		comboBox.setBounds(68, 266, 221, 24);

		for (RandomTypes type : RandomTypes.values()) {
			comboBox.addItem(type);
		}

		// best random option preselected (Christian)
		comboBox.setSelectedIndex(2);
		contentPanel.add(comboBox);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton(Messages.getString("NonogramChooserUI.ButtonOK"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save chosen options to vars
				if (rdbtnPredefinedNonograms.isSelected()) {
					type = 0;
				} else if (rdbtnRandomNonogram.isSelected()) {
					type = 1;
				} else if (rdbtnNonogramBySeed.isSelected()) {
					type = 2;
				}
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node != null) {
					if (node.getUserObject() instanceof Nonogram) {
						choosenNono = (Nonogram) node.getUserObject();
					}
				}
				randomType = (RandomTypes) comboBox.getSelectedItem();
				seed = txtNotYetImplemented.getText();

				if (type == 0 && choosenNono == null) {

				} else {
					validOptions = true;
					dispose();
				}

			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton(
				Messages.getString("NonogramChooserUI.ButtonCancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		rdbtnPredefinedNonograms = new JRadioButton(
				Messages.getString("NonogramChooserUI.PredefinedNonogramsText"));
		rdbtnPredefinedNonograms.setSelected(true);
		rdbtnPredefinedNonograms.setBounds(8, 8, 229, 23);
		contentPanel.add(rdbtnPredefinedNonograms);
		rdbtnPredefinedNonograms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rdbtnPredefinedNonograms.setSelected(true);
				tree.setEnabled(true);
				comboBox.setEnabled(false);
				txtNotYetImplemented.setEnabled(false);
				rdbtnNonogramBySeed.setSelected(false);
				rdbtnRandomNonogram.setSelected(false);
				sliderWidth.setEnabled(false);
				sliderHeight.setEnabled(false);
			}
		});

		rdbtnRandomNonogram = new JRadioButton(
				Messages.getString("NonogramChooserUI.RandomNonogramText"));
		rdbtnRandomNonogram.setBounds(8, 235, 260, 23);
		contentPanel.add(rdbtnRandomNonogram);
		rdbtnRandomNonogram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rdbtnRandomNonogram.setSelected(true);
				tree.setEnabled(false);
				comboBox.setEnabled(true);
				txtNotYetImplemented.setEnabled(false);
				rdbtnPredefinedNonograms.setSelected(false);
				rdbtnNonogramBySeed.setSelected(false);
				sliderWidth.setEnabled(true);
				sliderHeight.setEnabled(true);
			}
		});

		rdbtnNonogramBySeed = new JRadioButton(
				Messages.getString("NonogramChooserUI.NonogramBySeedText"));
		rdbtnNonogramBySeed.setBounds(8, 358, 229, 23);
		contentPanel.add(rdbtnNonogramBySeed);

		rdbtnNonogramBySeed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rdbtnNonogramBySeed.setSelected(true);
				tree.setEnabled(false);
				comboBox.setEnabled(false);
				txtNotYetImplemented.setEnabled(true);
				rdbtnPredefinedNonograms.setSelected(false);
				rdbtnRandomNonogram.setSelected(false);
				sliderWidth.setEnabled(false);
				sliderHeight.setEnabled(false);
			}
		});

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
				Messages.getString("NonogramChooserUI.NonogramsText"));
		treeModel = new DefaultTreeModel(rootNode);

		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					okButton.doClick();
				}
			}
		});

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(tree);
		scrollPane.setBounds(68, 39, 221, 188);
		contentPanel.add(scrollPane);

		sliderHeight = new JSlider();
		sliderHeight.setSnapToTicks(true);
		sliderHeight.setMajorTickSpacing(1);
		sliderHeight.setMinorTickSpacing(1);
		sliderHeight.setMinimum(5);
		sliderHeight.setMaximum(20);
		sliderHeight.setValue(10);
		sliderHeight.setBounds(138, 302, 112, 16);
		sliderHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				labelHeight.setText(String.valueOf(sliderHeight.getValue()));
			}
		});
		sliderHeight.setEnabled(false);
		contentPanel.add(sliderHeight);

		sliderWidth = new JSlider();
		sliderWidth.setSnapToTicks(true);
		sliderWidth.setMinorTickSpacing(1);
		sliderWidth.setMajorTickSpacing(1);
		sliderWidth.setMinimum(5);
		sliderWidth.setMaximum(20);
		sliderWidth.setValue(10);
		sliderWidth.setBounds(138, 330, 112, 16);
		sliderWidth.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				labelWidth.setText(String.valueOf(sliderWidth.getValue()));
			}
		});
		sliderWidth.setEnabled(false);
		contentPanel.add(sliderWidth);

		JLabel lblHhe = new JLabel(
				Messages.getString("NonogramChooserUI.HeightLabel"));
		lblHhe.setBounds(68, 302, 52, 15);
		contentPanel.add(lblHhe);

		JLabel lblBreite = new JLabel(
				Messages.getString("NonogramChooserUI.WidthLabel"));
		lblBreite.setBounds(68, 330, 52, 15);
		contentPanel.add(lblBreite);

		labelHeight = new JLabel(Integer.toString(sliderHeight.getValue()));
		labelHeight.setBounds(263, 302, 31, 15);
		contentPanel.add(labelHeight);

		labelWidth = new JLabel(Integer.toString(sliderWidth.getValue()));
		labelWidth.setBounds(263, 330, 31, 15);
		contentPanel.add(labelWidth);

	}

	public boolean isValidOptions() {
		return validOptions;
	}

	public int getType() {
		return type;
	}

	public Nonogram getChoosenNono() {
		return choosenNono;
	}

	public RandomTypes getRandomType() {
		return randomType;
	}

	public String getSeed() {
		return seed;
	}

	public void addNonogramsToTree(String rootNode, Object[] nonograms) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(rootNode);
		treeModel.insertNodeInto(top, (MutableTreeNode) treeModel.getRoot(), 0);

		for (int i = 0; i < nonograms.length; i++) {
			treeModel.insertNodeInto(new DefaultMutableTreeNode(nonograms[i]),
					top, 0);
		}
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	public int getSliderHeight() {
		return sliderHeight.getValue();
	}

	public int getSliderWidth() {
		return sliderWidth.getValue();
	}
}

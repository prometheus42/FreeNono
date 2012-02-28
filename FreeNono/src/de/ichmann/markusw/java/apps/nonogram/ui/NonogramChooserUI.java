package de.ichmann.markusw.java.apps.nonogram.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTree;

import de.ichmann.markusw.java.apps.nonogram.model.Nonogram;
import de.ichmann.markusw.java.apps.nonogram.model.RandomNonogram.RandomTypes;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NonogramChooserUI extends JDialog {

	private static final long serialVersionUID = -6359071603972829942L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtNotYetImplemented;
	private JComboBox comboBox;
	private JTree tree;
	public DefaultTreeModel treeModel;
	private JRadioButton rdbtnPredefinedNonograms;
	private JRadioButton rdbtnRandomNonogram;
	private JRadioButton rdbtnNonogramBySeed;
	private JScrollPane scrollPane;

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
		setModal(true);
		setBounds(100, 100, 308, 415);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		txtNotYetImplemented = new JTextField();
		txtNotYetImplemented.setText("Not yet implemented");
		txtNotYetImplemented.setEnabled(false);
		txtNotYetImplemented.setBounds(68, 323, 221, 19);
		contentPanel.add(txtNotYetImplemented);
		txtNotYetImplemented.setColumns(10);

		contentPanel.setLayout(null);

		JLabel lblSeed = new JLabel("Seed:");
		lblSeed.setBounds(18, 325, 52, 15);
		contentPanel.add(lblSeed);

		comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setEditable(false);
		comboBox.setBounds(68, 266, 221, 24);

		int tmp = RandomTypes.values().length;
		for (int i = 0; i < tmp; i++) {
			comboBox.insertItemAt(RandomTypes.values()[i], i);
		}

		contentPanel.add(comboBox);

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Nonogramme");
		treeModel = new DefaultTreeModel(rootNode);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save choosen options to var

				if (rdbtnPredefinedNonograms.isSelected()) {
					type = 0;
				} else if (rdbtnRandomNonogram.isSelected()) {
					type = 1;
				} else if (rdbtnNonogramBySeed.isSelected()) {
					type = 2;
				}
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node != null) {
					if (node.getUserObject().getClass() == Nonogram.class) {
						choosenNono = (Nonogram) node.getUserObject();
					}
				}
				randomType = (RandomTypes) comboBox.getSelectedItem();
				seed = txtNotYetImplemented.getText();

				validOptions = true;
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		rdbtnPredefinedNonograms = new JRadioButton("Predefined Nonograms");
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
			}
		});

		rdbtnRandomNonogram = new JRadioButton("Random Nonogram");
		rdbtnRandomNonogram.setBounds(8, 235, 229, 23);
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
			}
		});

		rdbtnNonogramBySeed = new JRadioButton("Nonogram by seed");
		rdbtnNonogramBySeed.setBounds(8, 294, 229, 23);
		contentPanel.add(rdbtnNonogramBySeed);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(68, 39, 221, 188);
		contentPanel.add(scrollPane);

		tree = new JTree(treeModel);
		scrollPane.setViewportView(tree);
		// tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		rdbtnNonogramBySeed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rdbtnNonogramBySeed.setSelected(true);
				tree.setEnabled(false);
				comboBox.setEnabled(false);
				txtNotYetImplemented.setEnabled(true);
				rdbtnPredefinedNonograms.setSelected(false);
				rdbtnRandomNonogram.setSelected(false);
			}
		});

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

}

/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Martin Wichmann, Christian Wichmann
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.CourseProvider;
import org.freenono.interfaces.NonogramProvider;
import org.freenono.model.Nonogram;
import org.freenono.provider.NonogramFromFilesystem;
import org.freenono.provider.NonogramFromSeed;

public class NonogramChooserUI extends JDialog {

	private static final long serialVersionUID = 449003977161113952L;

	private static Logger logger = Logger.getLogger(NonogramChooserUI.class);

	List<CollectionProvider> nonogramProvider = null;
	private Nonogram result = null;

	private JTree nonogramsTree = null;
	private DefaultTreeModel nonogramsTreeModel = null;
	private DefaultMutableTreeNode nonogramsTreeRootNode = null;

	private JSplitPane extraPane = null;
	private CourseViewPane courseViewPane = null;

	private JLabel labelInfoCourse = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoID = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoName = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoDesc = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoDiff = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoWidth = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoHeight = new JLabel(""); //$NON-NLS-1$

	private JPanel seedOptionPane = null;
	private JPanel randomOptionPane = null;
	private JPanel emptyOptionPane = null;
	private JSlider sliderHeight = null;
	private JSlider sliderWidth = null;
	private JLabel labelHeight = null;
	private JLabel labelWidth = null;
	private JTextField seed = null;

	private final int optionDividerLocation = 400;

	/**
	 * Create the dialog.
	 */
	public NonogramChooserUI(List<CollectionProvider> nonogramProvider) {

		// take provider for nonograms
		this.nonogramProvider = nonogramProvider;

		// set gui options
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setSize(1000, 800);
		this.setLayout(new BorderLayout());

		// add buttons to dialog
		add(getButtonPane(), BorderLayout.SOUTH);

		// split dialog horizontal between treePane and the extraPane
		JSplitPane horizontalSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, getTreePane(), getExtraPane());
		horizontalSplitPane.setContinuousLayout(true);
		horizontalSplitPane.setDividerLocation(300);
		horizontalSplitPane.setDividerSize(5);
		add(horizontalSplitPane, BorderLayout.CENTER);

		// populate tree
		populateTree(nonogramProvider);

		// expand tree
		DefaultMutableTreeNode currentNode = nonogramsTreeRootNode
				.getNextNode();
		do {
			if (currentNode.getLevel() == 1)
				nonogramsTree.expandPath(new TreePath(currentNode.getPath()));
			currentNode = currentNode.getNextNode();
		} while (currentNode != null);

	}

	private void populateTree(List<CollectionProvider> nonogramProvider) {

		Collection<CourseProvider> courseList = null;
		Collection<NonogramProvider> nonogramList = null;

		for (CollectionProvider np : nonogramProvider) {

			courseList = np.getCourseProvider();

			DefaultMutableTreeNode nonoRootNode = new DefaultMutableTreeNode(
					np.getProviderName());
			logger.debug("Adding provider " + np.getProviderName()
					+ " to tree.");

			nonogramsTreeModel.insertNodeInto(nonoRootNode, nonogramsTreeRootNode, 0);

			for (CourseProvider course : courseList) {

				DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(
						course);
				nonogramsTreeModel.insertNodeInto(dirNode, nonoRootNode,
						nonoRootNode.getChildCount());
				logger.debug("Adding course " + course + " to tree.");

				nonogramList = course.getNonogramProvider();
				for (NonogramProvider nono : nonogramList) {
					nonogramsTreeModel.insertNodeInto(new DefaultMutableTreeNode(nono),
							dirNode, dirNode.getChildCount());
					//logger.debug("Adding nonogram " + nono + " to tree.");
				}

			}

		}

	}

	private JSplitPane getExtraPane() {
		extraPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getInfoPane(),
				getOptionsPanes());
		extraPane.setContinuousLayout(true);
		extraPane.setEnabled(false);
		extraPane.setDividerLocation(optionDividerLocation);
		extraPane.setDividerSize(5);
		return extraPane;
	}

	private JPanel getOptionsPanes() {
		emptyOptionPane = new JPanel(new GridLayout());
		randomOptionPane = new JPanel();
		randomOptionPane.setLayout(new BoxLayout(randomOptionPane,
				BoxLayout.PAGE_AXIS));
		seedOptionPane = new JPanel(new FlowLayout());
		seedOptionPane.add(new JLabel(Messages
				.getString("NonogramChooserUI.SeedLabel"))); //$NON-NLS-1$
		seed = new JTextField();
		seed.setPreferredSize((new Dimension(300,
				seed.getPreferredSize().height)));
		seedOptionPane.add(seed);

		sliderHeight = new JSlider();
		sliderHeight.setSnapToTicks(true);
		sliderHeight.setMajorTickSpacing(1);
		sliderHeight.setMinorTickSpacing(1);
		sliderHeight.setMinimum(5);
		sliderHeight.setMaximum(20);
		sliderHeight.setValue(10);
		sliderHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				labelHeight.setText(String.valueOf(sliderHeight.getValue()));
			}
		});

		sliderWidth = new JSlider();
		sliderWidth.setSnapToTicks(true);
		sliderWidth.setMinorTickSpacing(1);
		sliderWidth.setMajorTickSpacing(1);
		sliderWidth.setMinimum(5);
		sliderWidth.setMaximum(20);
		sliderWidth.setValue(10);
		sliderWidth.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				labelWidth.setText(String.valueOf(sliderWidth.getValue()));
			}
		});

		JLabel lblHhe = new JLabel(
				Messages.getString("NonogramChooserUI.HeightLabel")); //$NON-NLS-1$
		lblHhe.setBounds(68, 302, 52, 15);

		JLabel lblBreite = new JLabel(
				Messages.getString("NonogramChooserUI.WidthLabel")); //$NON-NLS-1$
		lblBreite.setBounds(68, 330, 52, 15);

		labelHeight = new JLabel(Integer.toString(sliderHeight.getValue()));
		labelHeight.setBounds(263, 302, 31, 15);

		labelWidth = new JLabel(Integer.toString(sliderWidth.getValue()));
		labelWidth.setBounds(263, 330, 31, 15);

		randomOptionPane.add(lblHhe);
		randomOptionPane.add(sliderHeight);
		randomOptionPane.add(labelHeight);
		randomOptionPane.add(lblBreite);
		randomOptionPane.add(sliderWidth);
		randomOptionPane.add(labelWidth);

		return emptyOptionPane;
	}

	private JPanel getTreePane() {
		JPanel left = new JPanel(new GridLayout());
		nonogramsTreeRootNode = new DefaultMutableTreeNode(
				Messages.getString("NonogramChooserUI.FreeNono")); //$NON-NLS-1$
		nonogramsTreeModel = new DefaultTreeModel(nonogramsTreeRootNode);
		nonogramsTree = new JTree(nonogramsTreeModel);
		nonogramsTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		nonogramsTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handleMouseClick(e.getClickCount());
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(nonogramsTree);
		left.add(scrollPane);
		return left;
	}

	private JPanel getInfoPane() {
		JPanel top = new JPanel(new GridLayout(7, 2));
		top.add(new JLabel(Messages.getString("NonogramChooserUI.Course"))); //$NON-NLS-1$
		top.add(labelInfoCourse);
		top.add(new JLabel(Messages.getString("NonogramChooserUI.ID"))); //$NON-NLS-1$
		top.add(labelInfoID);
		top.add(new JLabel(Messages.getString("NonogramChooserUI.Name"))); //$NON-NLS-1$
		top.add(labelInfoName);
		top.add(new JLabel(Messages.getString("NonogramChooserUI.Desc"))); //$NON-NLS-1$
		top.add(labelInfoDesc);
		top.add(new JLabel(Messages.getString("NonogramChooserUI.Difficulty"))); //$NON-NLS-1$
		top.add(labelInfoDiff);
		top.add(new JLabel(Messages.getString("NonogramChooserUI.Width"))); //$NON-NLS-1$
		top.add(labelInfoWidth);
		top.add(new JLabel(Messages.getString("NonogramChooserUI.Height"))); //$NON-NLS-1$
		top.add(labelInfoHeight);
		return top;
	}

	/**
	 * This method builds the panel which includes the OK and Cancel buttons. By
	 * clicking on the OK button the ActionListener identifies the chosen
	 * nonogram to play and saves it in the result attribute.
	 * 
	 * @return button panel
	 */
	private JPanel getButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = null;
		okButton = new JButton("OK"); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				performOK();
			}

		});
		okButton.setActionCommand(Messages
				.getString("NonogramChooserUI.ButtonOK")); //$NON-NLS-1$
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton(
				Messages.getString("NonogramChooserUI.ButtonCancel")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel"); //$NON-NLS-1$
		buttonPane.add(cancelButton);

		return buttonPane;
	}

	/**
	 * Get choosen Nonogram
	 * 
	 * @return Nonogram, if one is choosen, else null
	 */
	public Nonogram getResult() {
		return result;
	}

	/**
	 * Analyze which element of the tree was last selected when OK button was
	 * pressed. If this element is of NonogramProvider the chosen Nonogram is
	 * fetched by the provider and saved as result.
	 */
	private void performOK() {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) nonogramsTree
				.getLastSelectedPathComponent();
		if (node != null) {
			
			// handle if nonogram was chosen -> save nonogram
			if (node.getUserObject() instanceof NonogramProvider) {
				
				if (node.getUserObject() instanceof NonogramFromSeed) {
					
					// handle nonogram from seed
					result = ((NonogramFromSeed) node.getUserObject())
							.plantSeed(seed.getText());
					dispose();
				} else if (node.getUserObject() instanceof NonogramFromFilesystem) {
					
					// handle nonogram from file system
					result = ((NonogramProvider) node.getUserObject())
							.fetchNonogram();
					dispose();
				}
			} else if (node.getUserObject() instanceof CourseProvider) {
				
				courseViewPane = new CourseViewPane((CourseProvider) node.getUserObject());
				extraPane.setLeftComponent(courseViewPane);
			}

			// TODO: handle if course was chosen -> start course
		}
	}

	/**
	 * Check which element of the tree is clicked by mouse. For a single click
	 * (see clickCount) the information for the chosen element are shown in the
	 * InfoPane, for a double click the chosen nonogram is saved by performOK().
	 * 
	 * @param clickCount
	 *            number of clicks (single click or double click)
	 */
	private void handleMouseClick(int clickCount) {
		DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) nonogramsTree
				.getLastSelectedPathComponent();

		if (tempNode != null) {
			Object temp = tempNode.getUserObject();
			if (clickCount == 1) {
				DefaultMutableTreeNode tempParent = (DefaultMutableTreeNode) tempNode
						.getParent();
				if (tempNode == nonogramsTreeModel.getRoot()) {
					
					return;
				}
				// update InfoPane dependent on chosen tree element
				// if (temp instanceof NonogramFromSeed) {
				//
				// extraPane.setBottomComponent(seedOptionPane);
				// extraPane.setDividerLocation(optionDividerLocation);
				// }
				// else if ((temp instanceof NonogramProvider)) {
				//
				// labelInfoCourse.setText(tempParent.getUserObject()
				// .toString());
				// labelInfoID.setText(((NonogramProvider) temp).getId());
				// labelInfoName.setText(((NonogramProvider) temp).getName());
				// labelInfoDesc.setText(((NonogramProvider)
				// temp).getDescription());
				// labelInfoDiff.setText(String.valueOf(((NonogramProvider)
				// temp)
				// .getDifficulty()));
				// labelInfoWidth.setText(String.valueOf(((NonogramProvider)
				// temp)
				// .width()));
				// labelInfoHeight.setText(String.valueOf(((NonogramProvider)
				// temp)
				// .height()));
				// extraPane.setBottomComponent(emptyOptionPane);
				// extraPane.setDividerLocation(optionDividerLocation);
				// } else {
				//
				// extraPane.setBottomComponent(emptyOptionPane);
				// extraPane.setDividerLocation(optionDividerLocation);
				// labelInfoCourse.setText("");
				// labelInfoID.setText("");
				// labelInfoName.setText("");
				// labelInfoDesc.setText("");
				// labelInfoDiff.setText("");
				// labelInfoWidth.setText("");
				// labelInfoHeight.setText("");
				// }
				
			} else if ((clickCount == 2)) {
				
				performOK();
			}
		}
	}

}

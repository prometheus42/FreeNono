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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
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
import org.freenono.provider.CourseFromSeed;
import org.freenono.provider.NonogramFromSeed;

public class NonogramChooserUI extends JDialog {

	private static final long serialVersionUID = 449003977161113952L;

	private static Logger logger = Logger.getLogger(NonogramChooserUI.class);

	List<CollectionProvider> nonogramProvider = null;
	private Nonogram chosenNonogram = null;

	private JTree nonogramsTree = null;
	private DefaultTreeModel nonogramsTreeModel = null;
	private DefaultMutableTreeNode nonogramsTreeRootNode = null;

	private JPanel extraPane = null;
	private CourseViewPane courseViewPane = null;
	private JPopupMenu popup = null;
	

	/**
	 * Create the dialog.
	 */
	public NonogramChooserUI(List<CollectionProvider> nonogramProvider) {

		this.nonogramProvider = nonogramProvider;

		initialize();
		
		addListener();
	}

	
	private void initialize() {

		// set gui options
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(DEFAULT_MODALITY_TYPE);

		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// add buttons to dialog
		add(getButtonPane(), BorderLayout.SOUTH);

		// split dialog horizontal between treePane and the extraPane
		JSplitPane horizontalSplitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, getTreePane(), getExtraPane());
		horizontalSplitPane.setContinuousLayout(true);
		// horizontalSplitPane.setDividerLocation(300);
		// horizontalSplitPane.setDividerSize(5);
		add(horizontalSplitPane, BorderLayout.NORTH);

		// populate tree
		populateTree(nonogramProvider);

		// expand tree
		DefaultMutableTreeNode currentNode = nonogramsTreeRootNode
				.getNextNode();
		do {
			
			if (currentNode.getLevel() == 1) {
				
				nonogramsTree.expandPath(new TreePath(currentNode.getPath()));
			}
			currentNode = currentNode.getNextNode();
			
		} while (currentNode != null);

		pack();
	}


	private void addListener() {

		nonogramsTree.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1) {

					if (e.getClickCount() == 1) {

						openCourseViewPane();
					} else if (e.getClickCount() == 2) {

						performOK();
					}

					// e.consume();

				} else if (e.isPopupTrigger()) {

					nonogramsTree.setSelectionRow(nonogramsTree
							.getClosestRowForLocation(e.getX(), e.getY()));

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) nonogramsTree
							.getLastSelectedPathComponent();

					if (node != null) {

						if (node.getUserObject() instanceof CourseFromSeed) {

							openCourseViewPane();

							showPopupMenu(e.getPoint());
						}
					}
				}
			}
		});
		
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("ESCAPE"), "Close");
		getRootPane().getActionMap().put("Close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				performClose();
			}
		});
		
		nonogramsTree.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("ENTER"), "OpenCourse");
		nonogramsTree.getActionMap().put("OpenCourse", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				openCourseViewPane();
			}
		});
	}

	
	/********************* building tree pane *********************/
	private JPanel getTreePane() {
		
		JPanel left = new JPanel(new GridLayout());
		nonogramsTreeRootNode = new DefaultMutableTreeNode(
				Messages.getString("NonogramChooserUI.FreeNono"));
		nonogramsTreeModel = new DefaultTreeModel(nonogramsTreeRootNode);
		nonogramsTree = new JTree(nonogramsTreeModel);
		nonogramsTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(nonogramsTree);
		left.add(scrollPane);
		
		return left;
	}
	
	private void populateTree(List<CollectionProvider> nonogramProvider) {

		List<CourseProvider> courseList = null;

		for (CollectionProvider np : nonogramProvider) {

			courseList = np.getCourseProvider();

			DefaultMutableTreeNode nonoRootNode = new DefaultMutableTreeNode(
					np.getProviderName());
			logger.debug("Adding provider " + np.getProviderName() 
					+ " to tree."); 

			nonogramsTreeModel.insertNodeInto(nonoRootNode,
					nonogramsTreeRootNode, 0);

			Collections.sort(courseList, CourseProvider.NAME_ASCENDING_ORDER);

			for (CourseProvider course : courseList) {

				DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(
						course);
				nonogramsTreeModel.insertNodeInto(dirNode, nonoRootNode,
						nonoRootNode.getChildCount());
				logger.debug("Adding course " + course + " to tree.");
			}
		}
	}

	/********************* building extra pane *********************/
	private JPanel getExtraPane() {

		extraPane = new JPanel();
		return extraPane;
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

		JButton cancelButton = new JButton(
				Messages.getString("NonogramChooserUI.ButtonCancel")); 
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				performClose();
			}
		});
		cancelButton.setActionCommand("Cancel"); 
		buttonPane.add(cancelButton);

		return buttonPane;
	}

	/**
	 * Check which element of the tree is clicked by mouse. For a single click
	 * the information for the chosen element are shown in the InfoPane.
	 */
	private void openCourseViewPane() {

		DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) nonogramsTree
				.getLastSelectedPathComponent();

		if (tempNode != null) {

			Object temp = tempNode.getUserObject();

			if (tempNode == nonogramsTreeModel.getRoot()) {
				return;
			}

			// if course is chosen, set up the CourseViewPane for this
			// course
			if (temp instanceof CourseProvider) {

				if (courseViewPane != null) {

					courseViewPane.setEnabled(false);
					extraPane.remove(courseViewPane);
					extraPane.validate();
				}

				courseViewPane = new CourseViewPane(this, (CourseProvider) temp);
				extraPane.add(courseViewPane);
				this.pack();
			}
		}
	}

	/**
	 * Handle right click on tree element and show popup menu if random
	 * nonogram course was chosen.
	 * 
	 * @param point
	 */
	private void showPopupMenu(Point point) {
		
		popup = new JPopupMenu();

		JMenuItem newSeed = new JMenuItem(
				Messages.getString("NonogramChooserUI.NewSeed")); 
		newSeed.addMouseListener(new MouseAdapter() {

			// ask user for seed and set seeded nongram as chosenNonogram
			public void mousePressed(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 1) {

					popup.setVisible(false);

					performOK();
				}
			}
		});

		JMenuItem clearSeeds = new JMenuItem(
				Messages.getString("NonogramChooserUI.ClearSeeds")); 
		clearSeeds.addMouseListener(new MouseAdapter() {

			// delete all seed thumbnails for solved nonograms from default
			// directory
			public void mousePressed(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1 
						&& e.getClickCount() == 1) {
				
					popup.setVisible(false);
					
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) nonogramsTree
							.getLastSelectedPathComponent();

					((CourseFromSeed) node.getUserObject()).clearSeeds();
					
					openCourseViewPane();
				}
			}
		});

		popup.add(newSeed);
		popup.add(clearSeeds);
		
		popup.show(nonogramsTree, point.x, point.y);
	}

	/**
	 * Analyze which element of the tree was last selected when OK button was
	 * pressed. If this element is of NonogramProvider the chosen Nonogram is
	 * fetched by the provider and saved as result.
	 */
	private void performOK() {

		// TODO: handle if course was chosen -> start whole course and hold
		// overall statistics for all nonograms of course?

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) nonogramsTree
				.getLastSelectedPathComponent();

		if (node != null) {

			// if seed course is chosen, prepare nonogram from user input (seed)
			if (node.getUserObject() instanceof CourseFromSeed) {

				askForSeed((CourseFromSeed) node.getUserObject());
			}
		}
	}
	
	private void performClose() {
		
		dispose();
	}


	private void askForSeed(CourseFromSeed course) {
		
		// ask user for seed
		String seed = JOptionPane.showInputDialog(this, Messages
				.getString("NonogramChooserUI.SeedLabel"), Messages 
				.getString("NonogramChooserUI.RandomNonogramText"), 
				JOptionPane.QUESTION_MESSAGE);

		// generate nonogram from seed and set it as chosenNonogram
		if (seed != null) {
			
			if (!seed.isEmpty()) {
				
				NonogramProvider np = course.generateSeededNonogram(seed);
				
				chosenNonogram = ((NonogramFromSeed) np).fetchNonogram();
				
				dispose();
			}
		}
	}

	/**
	 * Get choosen Nonogram
	 * 
	 * @return Nonogram, if one is chosen, else null
	 */
	public Nonogram getChosenNonogram() {

		return chosenNonogram;
	}

	public void setChosenNonogram(Nonogram n) {

		chosenNonogram = n;
		dispose();
	}

}

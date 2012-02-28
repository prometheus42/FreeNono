package org.freenono.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JSplitPane;

import org.freenono.model.Course;
import org.freenono.model.Manager;
import org.freenono.model.Nonogram;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NonogramChooserUI_New extends JDialog {

	private static final long serialVersionUID = 449003977161113952L;
	private Nonogram result = null;
	private DefaultTreeModel treeModel = null;
	private JTree tree = null;
	private DefaultMutableTreeNode rootNode = null;
	private JButton okButton = null;

	/**
	 * Create the dialog.
	 */
	public NonogramChooserUI_New(Manager manager) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 600, 600);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (node != null) {
						if (node.getUserObject() instanceof Nonogram) {
							result = (Nonogram) node.getUserObject();
						}
					}
					dispose();
				}
			});
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);

			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);

		}
		{

			// basic layout
			JPanel left = new JPanel(new GridLayout());
			JPanel right = new JPanel(new GridLayout());
			JPanel top = new JPanel(new GridLayout(7, 2));
			JPanel bottom = new JPanel(new GridLayout());
			JSplitPane splitPaneTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
			JSplitPane splitPaneOptions = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
			splitPaneTree.setContinuousLayout(true);
			splitPaneOptions.setContinuousLayout(true);
			splitPaneOptions.setEnabled(false);
			right.add(splitPaneOptions);
			splitPaneTree.setDividerLocation(300);
			splitPaneTree.setDividerSize(5);
			splitPaneOptions.setDividerLocation(180);
			splitPaneOptions.setDividerSize(5);

			// info panel
			JLabel labelInfoCourse = new JLabel("1");
			JLabel labelInfoID = new JLabel("2");
			JLabel labelInfoName = new JLabel("3");
			JLabel labelInfoDesc = new JLabel("4");
			JLabel labelInfoDiff = new JLabel("5");
			JLabel labelInfoWidth = new JLabel("6");
			JLabel labelInfoHeight = new JLabel("7");
			top.add(new JLabel("Kurs:"));
			top.add(labelInfoCourse);
			top.add(new JLabel("ID:"));
			top.add(labelInfoID);
			top.add(new JLabel("Name:"));
			top.add(labelInfoName);
			top.add(new JLabel("Desc:"));
			top.add(labelInfoDesc);
			top.add(new JLabel("Schwierigkeit:"));
			top.add(labelInfoDiff);
			top.add(new JLabel("Breite:"));
			top.add(labelInfoWidth);
			top.add(new JLabel("Höhe:"));
			top.add(labelInfoHeight);

			// tree
			rootNode = new DefaultMutableTreeNode(Messages.getString("NonogramChooserUI.NonogramsText"));
			treeModel = new DefaultTreeModel(rootNode);
			tree = new JTree(treeModel);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					DefaultMutableTreeNode temp = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if ((e.getClickCount() == 2) && (temp.getUserObject() instanceof Nonogram)) {
						okButton.doClick();
					}
				}
			});
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(tree);
			left.add(scrollPane);

			// populate tree
			Collection<Course> dirList = manager.getCourseList();

			for (Course dir : dirList) {
				DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(dir);
				Nonogram[] nonolist = dir.getNonograms();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
				treeModel.insertNodeInto(dirNode, root, root.getChildCount());
				for (Nonogram nono : nonolist) {
					treeModel.insertNodeInto(new DefaultMutableTreeNode(nono), dirNode, dirNode.getChildCount());
				}
			}

			getContentPane().add(splitPaneTree, BorderLayout.CENTER);
		}
	}

	/**
	 * Get choosen Nonogram
	 * 
	 * @return Nonogram, if one is choosen, else null
	 */
	public Nonogram getResult() {
		return result;
	}

}

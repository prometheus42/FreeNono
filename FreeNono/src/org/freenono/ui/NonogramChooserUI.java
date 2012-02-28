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
import javax.swing.tree.TreeSelectionModel;

import org.freenono.model.Course;
import org.freenono.model.Manager;
import org.freenono.model.Nonogram;
import org.freenono.model.RandomNonogram;
import org.freenono.model.RandomNonogram.RandomTypes;

public class NonogramChooserUI extends JDialog {

	private static final long serialVersionUID = 449003977161113952L;
	private Nonogram result = null;
	private DefaultTreeModel treeModel = null;
	private JTree tree = null;
	private DefaultMutableTreeNode rootNode = null;
	private JButton okButton = null;
	private JLabel labelInfoCourse = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoID = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoName = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoDesc = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoDiff = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoWidth = new JLabel(""); //$NON-NLS-1$
	private JLabel labelInfoHeight = new JLabel(""); //$NON-NLS-1$
	private JPanel seedOptionPanel = null;
	private JPanel randomOptionPanel = null;
	private JPanel bottom = null;
	private JSplitPane splitPaneOptions = null;
	private JSlider sliderHeight = null;
	private JSlider sliderWidth = null;
	private JLabel labelHeight = null;
	private JLabel labelWidth = null;
	private JTextField seed = null;

	private final int optionDividerLocation = 400;

	/**
	 * Create the dialog.
	 */
	public NonogramChooserUI(Manager manager) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 800, 600);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			okButton = new JButton("OK"); //$NON-NLS-1$
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (node != null) {
						if (node.getUserObject() instanceof Nonogram) {
							result = (Nonogram) node.getUserObject();
							dispose();
						}
						if (node.getUserObject() instanceof String) {
							if (node.getUserObject().equals(Messages.getString("NonogramChooserUI.NonogramBySeedText"))) { //$NON-NLS-1$
								// TODO: implement nonograms by seed
								// result = ???
								dispose();
							}
						}
						if (node.getUserObject() instanceof RandomTypes) {
							RandomNonogram randomNono = new RandomNonogram();
							result = randomNono.createRandomNonogram(sliderHeight.getValue(), sliderWidth.getValue(), (RandomTypes) node.getUserObject());
							dispose();
						}
					}

				}
			});
			okButton.setActionCommand(Messages.getString("NonogramChooserUI.ButtonOK")); //$NON-NLS-1$
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);

			JButton cancelButton = new JButton(Messages.getString("NonogramChooserUI.ButtonCancel")); //$NON-NLS-1$
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			cancelButton.setActionCommand("Cancel"); //$NON-NLS-1$
			buttonPane.add(cancelButton);

		}
		{

			// basic layout
			JPanel left = new JPanel(new GridLayout());
			JPanel right = new JPanel(new GridLayout());
			JPanel top = new JPanel(new GridLayout(7, 2));
			bottom = new JPanel(new GridLayout());
			JSplitPane splitPaneTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
			splitPaneOptions = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
			splitPaneTree.setContinuousLayout(true);
			splitPaneOptions.setContinuousLayout(true);
			splitPaneOptions.setEnabled(false);
			right.add(splitPaneOptions);
			splitPaneTree.setDividerLocation(300);
			splitPaneTree.setDividerSize(5);
			splitPaneOptions.setDividerLocation(optionDividerLocation);
			splitPaneOptions.setDividerSize(5);

			randomOptionPanel = new JPanel();
			randomOptionPanel.setLayout(new BoxLayout(randomOptionPanel, BoxLayout.PAGE_AXIS));
			seedOptionPanel = new JPanel(new FlowLayout());
			seedOptionPanel.add(new JLabel(Messages.getString("NonogramChooserUI.SeedLabel"))); //$NON-NLS-1$
			seed = new JTextField();
			seed.setPreferredSize((new Dimension(300, seed.getPreferredSize().height)));
			seedOptionPanel.add(seed);

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

			JLabel lblHhe = new JLabel(Messages.getString("NonogramChooserUI.HeightLabel")); //$NON-NLS-1$
			lblHhe.setBounds(68, 302, 52, 15);

			JLabel lblBreite = new JLabel(Messages.getString("NonogramChooserUI.WidthLabel")); //$NON-NLS-1$
			lblBreite.setBounds(68, 330, 52, 15);

			labelHeight = new JLabel(Integer.toString(sliderHeight.getValue()));
			labelHeight.setBounds(263, 302, 31, 15);

			labelWidth = new JLabel(Integer.toString(sliderWidth.getValue()));
			labelWidth.setBounds(263, 330, 31, 15);

			randomOptionPanel.add(lblHhe);
			randomOptionPanel.add(sliderHeight);
			randomOptionPanel.add(labelHeight);
			randomOptionPanel.add(lblBreite);
			randomOptionPanel.add(sliderWidth);
			randomOptionPanel.add(labelWidth);

			// info panel
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

			// tree
			rootNode = new DefaultMutableTreeNode(Messages.getString("NonogramChooserUI.FreeNono")); //$NON-NLS-1$
			treeModel = new DefaultTreeModel(rootNode);
			tree = new JTree(treeModel);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					Object temp = tempNode.getUserObject();
					if (e.getClickCount() == 1) {
						DefaultMutableTreeNode tempParent = (DefaultMutableTreeNode) tempNode.getParent();
						if (tempNode == treeModel.getRoot()) {
							return;
						}
						// update infos
						if ((temp instanceof Nonogram)) {
							labelInfoCourse.setText(tempParent.getUserObject().toString());
							labelInfoID.setText(((Nonogram) temp).getId());
							labelInfoName.setText(((Nonogram) temp).getName());
							labelInfoDesc.setText(((Nonogram) temp).getDescription());
							labelInfoDiff.setText(String.valueOf(((Nonogram) temp).getDifficulty()));
							labelInfoWidth.setText(String.valueOf(((Nonogram) temp).width()));
							labelInfoHeight.setText(String.valueOf(((Nonogram) temp).height()));
							splitPaneOptions.setBottomComponent(bottom);
							splitPaneOptions.setDividerLocation(optionDividerLocation);
						} else if (temp instanceof RandomTypes) {
							splitPaneOptions.setBottomComponent(randomOptionPanel);
							splitPaneOptions.setDividerLocation(optionDividerLocation);
						} else if (temp instanceof String && temp.equals(Messages.getString("NonogramChooserUI.NonogramBySeedText"))) { //$NON-NLS-1$
							splitPaneOptions.setBottomComponent(seedOptionPanel);
							splitPaneOptions.setDividerLocation(optionDividerLocation);
						} else {
							splitPaneOptions.setBottomComponent(bottom);
							splitPaneOptions.setDividerLocation(optionDividerLocation);
							labelInfoCourse.setText(""); //$NON-NLS-1$
							labelInfoID.setText(""); //$NON-NLS-1$
							labelInfoName.setText(""); //$NON-NLS-1$
							labelInfoDesc.setText(""); //$NON-NLS-1$
							labelInfoDiff.setText(""); //$NON-NLS-1$
							labelInfoWidth.setText(""); //$NON-NLS-1$
							labelInfoHeight.setText(""); //$NON-NLS-1$
						}
					}
					if ((e.getClickCount() == 2)) {
						okButton.doClick();
					}
				}
			});
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(tree);
			left.add(scrollPane);

			// populate tree
			Collection<Course> dirList = manager.getCourseList();

			DefaultMutableTreeNode nonoRootNode = new DefaultMutableTreeNode(Messages.getString("NonogramChooserUI.NonogramsText")); //$NON-NLS-1$
			treeModel.insertNodeInto(nonoRootNode, rootNode, 0);

			for (Course dir : dirList) {
				DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(dir);
				Nonogram[] nonolist = dir.getNonograms();
				// DefaultMutableTreeNode root = (DefaultMutableTreeNode)
				// treeModel.getRoot();
				treeModel.insertNodeInto(dirNode, nonoRootNode, nonoRootNode.getChildCount());
				for (Nonogram nono : nonolist) {
					treeModel.insertNodeInto(new DefaultMutableTreeNode(nono), dirNode, dirNode.getChildCount());
				}
			}

			DefaultMutableTreeNode extrasRootNode = new DefaultMutableTreeNode(Messages.getString("NonogramChooserUI.Extras")); //$NON-NLS-1$
			treeModel.insertNodeInto(extrasRootNode, rootNode, rootNode.getChildCount());

			DefaultMutableTreeNode randomRootNode = new DefaultMutableTreeNode(Messages.getString("NonogramChooserUI.Random")); //$NON-NLS-1$
			treeModel.insertNodeInto(randomRootNode, extrasRootNode, extrasRootNode.getChildCount());

			RandomTypes[] randomTypes = RandomNonogram.RandomTypes.values();
			for (RandomTypes type : randomTypes) {
				treeModel.insertNodeInto(new DefaultMutableTreeNode(type), randomRootNode, randomRootNode.getChildCount());
			}

			treeModel.insertNodeInto(new DefaultMutableTreeNode(Messages.getString("NonogramChooserUI.NonogramBySeedText")), extrasRootNode, extrasRootNode.getChildCount()); //$NON-NLS-1$

			tree.expandRow(0);
			tree.expandRow(1);
			tree.expandRow(tree.getRowCount() - 1);

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

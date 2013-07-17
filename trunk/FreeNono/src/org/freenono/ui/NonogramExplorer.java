/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 by FreeNono Development Team
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.freenono.provider.CollectionFromFilesystem;
import org.freenono.provider.CollectionFromSeed;
import org.freenono.provider.CollectionFromServer;
import org.freenono.provider.CollectionProvider;
import org.freenono.provider.CourseProvider;
import org.freenono.provider.NonogramProvider;
import org.freenono.ui.colormodel.ColorModel;

/**
 * Shows a dialog for the user to administrate nonogram collections and choose a
 * nonogram to play. (Replacing NonogramChooser class.)
 * 
 * @author Christian Wichmann
 */
public class NonogramExplorer extends JDialog {

    private static final long serialVersionUID = 4250625963548539930L;

    private static Logger logger = Logger.getLogger(NonogramExplorer.class);

    private GridBagLayout layout;
    private JTabbedPane collectionPane;
    private JPanel maintenancePane;
    private JPanel collectionMaintenancePane;

    private final List<CollectionProvider> nonogramProvider;
    private final List<CourseProvider> coursesAlreadyAdded;
    private final List<NonogramExplorerTabComponent> tabHeaderList;
    private final ColorModel colorModel;

    private NonogramProvider chosenNonogram = null;

    /**
     * Color for nonograms with difficulty 'easiest'.
     */
    public static final Color EASIEST_COLOR = new Color(122, 255, 123);

    /**
     * Color for nonograms with difficulty 'easy'.
     */
    public static final Color EASY_COLOR = new Color(123, 152, 255);

    /**
     * Color for nonograms with difficulty 'normal'.
     */
    public static final Color NORMAL_COLOR = new Color(255, 246, 117);

    /**
     * Color for nonograms with difficulty 'hard'.
     */
    public static final Color HARD_COLOR = new Color(255, 187, 113);

    /**
     * Color for nonograms with difficulty 'hardest'.
     */
    public static final Color HARDEST_COLOR = new Color(255, 113, 113);

    /**
     * Color for nonograms with difficulty 'undefined'.
     */
    public static final Color UNDEFINED_COLOR = new Color(128, 128, 128);

    /**
     * Initializes a new NonogramExplorer.
     * 
     * @param nonogramProvider
     *            list of collections containing nonogram courses
     * @param colorModel
     *            color model given by settings
     */
    public NonogramExplorer(final List<CollectionProvider> nonogramProvider,
            final ColorModel colorModel) {

        this.nonogramProvider = nonogramProvider;
        this.colorModel = colorModel;

        coursesAlreadyAdded = new ArrayList<CourseProvider>();
        tabHeaderList = new ArrayList<NonogramExplorerTabComponent>();

        UIManager.put("TabbedPane.contentAreaColor ", Color.GREEN);
        UIManager.put("TabbedPane.selected", colorModel.getTopColor());
        UIManager.put("TabbedPane.background", Color.GREEN);
        UIManager.put("TabbedPane.shadow", Color.GREEN);
        UIManager.put("TabbedPane.borderColor", Color.RED);
        UIManager.put("TabbedPane.darkShadow", Color.RED);
        UIManager.put("TabbedPane.light", Color.RED);
        UIManager.put("TabbedPane.highlight", Color.RED);
        UIManager.put("TabbedPane.focus", Color.RED);
        UIManager.put("TabbedPane.unselectedBackground", Color.RED);
        UIManager.put("TabbedPane.selectHighlight", Color.RED);
        UIManager.put("TabbedPane.tabAreaBackground", Color.RED);
        UIManager.put("TabbedPane.borderHightlightColor", Color.RED);

        initialize();

        addListeners();
    }

    /**
     * Adds listener for mouse wheel events.
     */
    private void addListeners() {

        collectionPane.addMouseWheelListener(new MouseWheelListener() {

            /*
             * Code for mouse wheel listener stolen from:
             * http://www.jroller.com/
             * pago/entry/improving_jtabbedpanes_mouse_support_like
             */

            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                JTabbedPane tabPane = (JTabbedPane) e.getSource();
                int dir = e.getWheelRotation();
                int selIndex = tabPane.getSelectedIndex();
                int maxIndex = tabPane.getTabCount() - 1;
                if ((selIndex == 0 && dir < 0)
                        || (selIndex == maxIndex && dir > 0)) {
                    selIndex = maxIndex - selIndex;
                } else {
                    selIndex += dir;
                }
                tabPane.setSelectedIndex(selIndex);
            }
        });

        /**
         * Action class for context menu in tabbed pane.
         * 
         * Source: http://www.jroller.com/pago/entry/
         * improving_jtabbedpanes_mouse_support_like
         */
        class SelectTabAction extends AbstractAction {

            private static final long serialVersionUID = -3652726963206234089L;

            private JTabbedPane tabPane;
            private int index;

            /**
             * Default constructor.
             * 
             * @param tabPane
             *            tabbed pane on which context menu is shown
             * @param index
             *            index of this action in context menu
             */
            public SelectTabAction(final JTabbedPane tabPane, final int index) {
                super(tabPane.getTitleAt(index), tabPane.getIconAt(index));

                this.tabPane = tabPane;
                this.index = index;
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                tabPane.setSelectedIndex(index);
            }
        }

        collectionPane.addMouseListener(new MouseAdapter() {

            public void mouseClicked(final MouseEvent e) {
                // we only look at the right button
                if (SwingUtilities.isRightMouseButton(e)) {
                    JTabbedPane tabPane = (JTabbedPane) e.getSource();
                    JPopupMenu menu = new JPopupMenu();

                    int tabCount = tabPane.getTabCount();
                    for (int i = 0; i < tabCount; i++) {
                        menu.add(new SelectTabAction(tabPane, i));
                    }

                    menu.show(tabPane, e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Initializes a dialog to chose and administer collections and courses.
     */
    private void initialize() {

        // set gui options
        setTitle("NonogramExplorer");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setUndecorated(true);
        getContentPane().setBackground(colorModel.getTopColor());
        getContentPane().setForeground(colorModel.getBottomColor());
        ((JPanel) getContentPane()).setBorder(BorderFactory
                .createEtchedBorder());
        setIconImage(new ImageIcon(getClass().getResource(
                "/resources/icon/icon_freenono.png")).getImage());

        // set layout manager
        GridBagConstraints c = new GridBagConstraints();
        layout = new GridBagLayout();
        setLayout(layout);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(buildTabbedPane(), c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(buildButtonPanel(), c);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Builds a tab pane that will display the course views later.
     * 
     * @return tab pane
     */
    private JTabbedPane buildTabbedPane() {

        logger.debug("Building tab panel for courses...");

        if (collectionPane == null) {

            collectionPane = new JTabbedPane(JTabbedPane.LEFT,
                    JTabbedPane.SCROLL_TAB_LAYOUT);
            final int borderWidth = 15;
            collectionPane.setBorder(BorderFactory.createEmptyBorder(
                    borderWidth, borderWidth, borderWidth, borderWidth));
            collectionPane.setOpaque(false);

            for (CollectionProvider collection : nonogramProvider) {

                addCollectionTab(collection);
            }

            // set size of all tab header elements to maximum
            collectionPane.doLayout();
            int maxTabComponentWidth = 0;
            int maxtabComponentHeight = 0;
            for (NonogramExplorerTabComponent tab : tabHeaderList) {
                maxTabComponentWidth = tab.getWidth() > maxTabComponentWidth ? tab
                        .getWidth() : maxTabComponentWidth;
                maxtabComponentHeight = tab.getHeight() > maxtabComponentHeight ? tab
                        .getHeight() : maxtabComponentHeight;
            }
            for (NonogramExplorerTabComponent tab : tabHeaderList) {
                tab.setSize(new Dimension(maxTabComponentWidth,
                        maxtabComponentHeight));
                tab.validate();
                tab.repaint();
            }
            // collectionPane.doLayout();
            // collectionPane.revalidate();
            // collectionPane.setBorder(BorderFactory.createEmptyBorder());

            // collectionPane.setUI(new BasicTabbedPaneUI() {
            // // see
            // http://stackoverflow.com/questions/7054466/how-can-i-change-the-shape-of-a-jtabbedpane-tab/7056093#7056093
            // // and
            // http://stackoverflow.com/questions/11333946/colorize-a-tab-in-a-jtabbedpane-using-java-swing?lq=1
            // // @Override
            // // protected void paintTabArea(Graphics g, int tabPlacement, int
            // // selectedIndex) {
            // //
            // // }
            // boolean tabsOverlapBorder = false;
            //
            // // @Override
            // // protected void paintTab(Graphics g, int tabPlacement,
            // // Rectangle[] rects, int tabIndex, Rectangle iconRect,
            // // Rectangle textRect) {
            // //
            // // }
            // });
            // collectionPane.addChangeListener(new ChangeListener() {
            // @Override
            // public void stateChanged(final ChangeEvent e) {
            // for (NonogramExplorerTabComponent tab : tabHeaderList) {
            // tab.setOpaque(true);
            // tab.repaint();
            // }
            // NonogramExplorerTabComponent x = ((NonogramExplorerTabComponent)
            // (collectionPane
            // .getTabComponentAt(collectionPane
            // .getSelectedIndex())));
            // x.setOpaque(false);
            // x.repaint();
            // }
            // });
        }

        return collectionPane;
    }

    /**
     * Adds tabs for all courses in a collection.
     * 
     * @param collection
     *            collection to be added
     */
    private void addCollectionTab(final CollectionProvider collection) {

        ImageIcon icon = null;

        // get image dependent on the collection type
        if (collection instanceof CollectionFromFilesystem) {

            icon = new ImageIcon(getClass().getResource(
                    "/resources/icon/CollectionFromFilesystem.png"));
        } else if (collection instanceof CollectionFromServer) {

            icon = new ImageIcon(getClass().getResource(
                    "/resources/icon/CollectionFromServer.png"));
        } else if (collection instanceof CollectionFromSeed) {

            icon = new ImageIcon(getClass().getResource(
                    "/resources/icon/CollectionFromSeed.png"));
        }

        // add tabs for all courses that were not already added...
        for (CourseProvider course : collection.getCourseProvider()) {

            boolean skipCourse = false;
            for (CourseProvider tempCourse : coursesAlreadyAdded) {

                if (tempCourse.getCourseName().equals(course.getCourseName())) {
                    skipCourse = true;
                    break;
                }
            }

            if (!skipCourse) {

                // set component to paint tab
                JPanel tabContent = buildCoursePane(course);
                NonogramExplorerTabComponent tabHeader = new NonogramExplorerTabComponent(
                        course, icon);
                collectionPane.addTab(course.getCourseName(), tabContent);
                final int tabNumber = collectionPane
                        .indexOfComponent(tabContent);
                collectionPane.setTabComponentAt(tabNumber, tabHeader);
                // collectionPane.setMnemonicAt(tabNumber, KeyEvent.VK_1);

                tabHeaderList.add(tabHeader);
                coursesAlreadyAdded.add(course);
            }
        }
    }

    /**
     * Builds a pane containing the course view.
     * 
     * @param course
     *            course to be shown in pane
     * @return course view pane
     */
    private JPanel buildCoursePane(final CourseProvider course) {

        CourseViewPane panel = new CourseViewPane(course);
        return panel;
    }

    /**
     * Returns a panel containing all buttons on the lower margin of this
     * dialog.
     * 
     * @return panel with all buttons
     */
    private JPanel buildButtonPanel() {

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setOpaque(false);

        JButton maintenanceButton = new JButton(new ImageIcon(getClass()
                .getResource("/resources/icon/CollectionMaintenance.png")));
        maintenanceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });
        buttonPanel.add(maintenanceButton, BorderLayout.WEST);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });
        buttonPanel.add(cancelButton, BorderLayout.EAST);

        final int borderWidth = 15;
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, borderWidth,
                borderWidth, borderWidth));

        return buttonPanel;
    }

    /**
     * Builds a tab pane for adding and removing collections.
     * 
     * @return tab pane
     */
    @SuppressWarnings("unused")
    private JPanel buildMaintenanceTab() {

        maintenancePane = new JPanel();

        // set layout manager and constraints
        maintenancePane.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 4;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        // add listBox
        String[] data = new String[nonogramProvider.size()];
        int i = 0;
        for (CollectionProvider collection : nonogramProvider) {
            data[i++] = collection.getProviderName();
        }
        JList collectionList = new JList();
        // collectionList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        // collectionList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        JScrollPane listScroller = new JScrollPane(collectionList);
        listScroller.setPreferredSize(new Dimension(250, 400));
        maintenancePane.add(listScroller, c);

        // add buttons
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.NONE;
        maintenancePane.add(new JButton("-"), c);

        c.gridx = 2;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        maintenancePane.add(new JButton("+"), c);

        // add selection for new collection
        c.gridx = 4;
        c.gridy = 0;
        c.gridheight = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        JRadioButton collectionFilesystemButton = new JRadioButton(
                "Collection from Filesystem");
        JRadioButton collectionSeedButton = new JRadioButton(
                "Collection from Seed");
        JRadioButton collectionServerButton = new JRadioButton(
                "Collection from Server");
        ButtonGroup group = new ButtonGroup();
        group.add(collectionFilesystemButton);
        group.add(collectionSeedButton);
        group.add(collectionServerButton);
        JPanel radioButtonPane = new JPanel(new GridLayout(0, 1));
        radioButtonPane.add(collectionFilesystemButton);
        radioButtonPane.add(collectionSeedButton);
        radioButtonPane.add(collectionServerButton);
        maintenancePane.add(radioButtonPane, c);

        // collection maintenance pane
        c.gridx = 4;
        c.gridy = 4;
        c.gridheight = 2;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        collectionMaintenancePane = new JPanel();
        maintenancePane.add(collectionMaintenancePane, c);

        // set listener for radio buttons
        ActionListener showFilesystemPane = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                collectionMaintenancePane.removeAll();
                collectionMaintenancePane.add(new JButton("Filesystem"));
                collectionMaintenancePane.validate();
            }
        };
        ActionListener showServerPane = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                collectionMaintenancePane.removeAll();
                collectionMaintenancePane.add(new JButton("Server"));
                collectionMaintenancePane.validate();
            }
        };
        collectionFilesystemButton.addActionListener(showFilesystemPane);
        // collectionSeedButton.addActionListener(showSeedPane);
        collectionServerButton.addActionListener(showServerPane);

        return maintenancePane;
    }

    /**
     * Returns chosen nonogram pattern.
     * 
     * @return chosen nonogram pattern
     */
    public final NonogramProvider getChosenNonogram() {

        if (chosenNonogram != null) {
            return chosenNonogram;

        } else {
            if (collectionPane.getSelectedComponent() instanceof CourseViewPane) {
                return ((CourseViewPane) collectionPane.getSelectedComponent())
                        .getChosenNonogram();
            }
        }
        return null;
    }
}

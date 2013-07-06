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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.freenono.model.Nonogram;
import org.freenono.provider.CollectionFromFilesystem;
import org.freenono.provider.CollectionFromSeed;
import org.freenono.provider.CollectionFromServer;
import org.freenono.provider.CollectionProvider;
import org.freenono.provider.CourseProvider;
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

    // private GridBagLayout layout;
    private JTabbedPane collectionPane;
    private JPanel maintenancePane;
    private JPanel collectionMaintenancePane;

    private List<CollectionProvider> nonogramProvider;
    private Nonogram chosenNonogram = null;

    private ColorModel colorModel;

    /**
     * Initializes a new NonogramExplorer.
     * 
     * @param nonogramProvider list of collections containing nonogram courses
     * @param colorModel color model given by settings
     */
    public NonogramExplorer(final List<CollectionProvider> nonogramProvider,
            final ColorModel colorModel) {

        this.nonogramProvider = nonogramProvider;
        this.colorModel = colorModel;

        initialize();
    }

    /**
     * Initializes a dialog to chose and administer collections and courses.
     */
    private void initialize() {

        final int width = 600;
        final int height = 600;
        
        // set gui options
        setSize(width, height);
        setTitle("NonogramExplorer");
        setLocationRelativeTo(null);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBackground(colorModel.getTopColor());
        setForeground(colorModel.getBottomColor());
        // setUndecorated(true);
        setIconImage(new ImageIcon(getClass().getResource(
                "/resources/icon/icon_freenono.png")).getImage());

        // set layout manager
        // layout = new GridBagLayout();
        // setLayout(layout);
        // GridBagConstraints c = new GridBagConstraints();
        // c.gridx = 0;
        // c.gridy = 0;
        // c.gridheight = 1;
        // c.gridwidth = 1;
        // c.anchor = GridBagConstraints.CENTER;
        // c.fill = GridBagConstraints.BOTH;

        add(buildTabbedPane());
        pack();
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

            for (CollectionProvider collection : nonogramProvider) {

                addCollectionTab(collection);
            }

        }

        // add maintenance tab
        collectionPane.addTab("Maintenance", new ImageIcon(getClass()
                .getResource("/resources/icon/CollectionMaintenance.png")),
                buildMaintenanceTab(), "Maintenance");

        return collectionPane;
    }

    /**
     * Adds tabs for all courses in a collection.
     * 
     * @param collection collection to be added
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

        // add tabs for all courses in collection
        for (CourseProvider course : collection.getCourseProvider()) {

            collectionPane.addTab(course.getCourseName(), icon,
                    buildCoursePane(course), "/home/christian/.freenono/...");

            // set component to paint tab
            NonogramExplorerTabComponent netc = new NonogramExplorerTabComponent(
                    course.getCourseName(), icon);
            collectionPane.setTabComponentAt(0, netc);

            // set mnemonic for tab
            // collectionPane.setMnemonicAt(0, KeyEvent.VK_1);
        }
    }

    /**
     * Builds a pane containing the course view.
     * 
     * @param course course to be shown in pane
     * @return course view pane
     */
    private JPanel buildCoursePane(final CourseProvider course) {

        // CourseViewPane panel = new CourseViewPane(this, course);
        JPanel jp = new JPanel();
        jp.add(new JLabel(new ImageIcon(getClass().getResource(
                "/resources/icon/splashscreen.png"))));
        return jp;
    }

    /**
     * Builds a tab pane for adding and removing collections.
     * 
     * @return tab pane
     */
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
    public final Nonogram getChosenNonogram() {

        return chosenNonogram;
    }

}

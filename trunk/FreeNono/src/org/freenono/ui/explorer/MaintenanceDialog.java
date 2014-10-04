/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2014 by FreeNono Development Team
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
package org.freenono.ui.explorer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.freenono.provider.CollectionProvider;
import org.freenono.ui.common.FreeNonoDialog;

/**
 * Shows a dialog to do maintenance for the nonogram collection. New collection
 * can be added and the given collection can be removed or their path changed.
 * 
 * @author Christian Wichmann
 */
public class MaintenanceDialog extends FreeNonoDialog {

    private static final long serialVersionUID = -4146790950090663458L;

    private JPanel maintenancePane;
    private JPanel collectionMaintenancePane;

    private List<CollectionProvider> nonogramProvider;

    /**
     * Initializes a new dialog to do maintenance for the nonogram collection.
     * 
     * @param owner
     *            parent component
     * @param foregroundColor
     *            foreground color
     * @param backgroundColor
     *            background color
     * @param nonogramProvider
     *            list of all nonogram collection provider
     */
    public MaintenanceDialog(final Frame owner, final Color foregroundColor, final Color backgroundColor,
            final List<CollectionProvider> nonogramProvider) {

        super(owner, foregroundColor, backgroundColor);

        this.nonogramProvider = nonogramProvider;

        initialize();
    }

    /**
     * Initializes a new dialog to do maintenance for the nonogram collection.
     */
    private void initialize() {

        add(buildMaintenancePane());
        pack();
        setVisible(true);
    }

    /**
     * Builds a tab pane for adding and removing collections.
     * 
     * @return tab pane
     */
    private JPanel buildMaintenancePane() {

        maintenancePane = new JPanel();
        maintenancePane.setOpaque(false);

        // set layout manager and constraints
        maintenancePane.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        final int inset = 10;
        c.insets = new Insets(inset, inset, inset, inset);
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
        JList<String> collectionList = new JList<>();
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
        JRadioButton collectionFilesystemButton = new JRadioButton("Collection from Filesystem");
        JRadioButton collectionSeedButton = new JRadioButton("Collection from Seed");
        JRadioButton collectionServerButton = new JRadioButton("Collection from Server");
        ButtonGroup group = new ButtonGroup();
        group.add(collectionFilesystemButton);
        group.add(collectionSeedButton);
        group.add(collectionServerButton);
        JPanel radioButtonPane = new JPanel(new GridLayout(0, 1));
        radioButtonPane.setOpaque(false);
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
        collectionMaintenancePane.setOpaque(false);
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

        c.gridx = 5;
        c.gridy = 5;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });
        maintenancePane.add(closeButton, c);

        return maintenancePane;
    }
}

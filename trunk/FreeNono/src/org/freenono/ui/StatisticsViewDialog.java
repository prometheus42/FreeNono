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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.model.SimpleStatistics;

import com.kitfox.svg.app.beans.SVGPanel;

/**
 * Shows a statistics dialog with information concerning the current or last
 * played game.
 * 
 * @author Christian Wichmann
 */
public class StatisticsViewDialog extends JDialog {

    private static final long serialVersionUID = -185463984939167375L;

    private static Logger logger = Logger.getLogger(StatisticsViewDialog.class);

    private Settings settings = null;

    private static final int SVG_WIDTH = 75;
    private static final int SVG_HEIGHT = 50;

    private JPanel contentPanel = null;

    /**
     * Initializes a new dialog to view statistics.
     * 
     * @param settings
     *            Settings object for background color.
     */
    public StatisticsViewDialog(final Settings settings) {

        this.settings = settings;

        initialize();
    }

    /**
     * Initializes this StatisticsViewDialog.
     */
    private void initialize() {

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(Messages.getString("StatisticsViewDialog.Title"));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setUndecorated(true);

        getContentPane().add(buildContentPane());

        pack();

        setLocationRelativeTo(null);
    }

    /**
     * Builds a panel to hold all components.
     * 
     * @return Content panel.
     */
    private JPanel buildContentPane() {

        if (contentPanel == null) {

            SimpleStatistics stats = SimpleStatistics.getInstance();

            contentPanel = new JPanel();

            contentPanel.setBackground(settings.getColorModel().getTopColor());
            contentPanel.setForeground(settings.getColorModel()
                    .getBottomColor());
            contentPanel.setBorder(BorderFactory.createEtchedBorder());

            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            final int inset = 10;
            c.insets = new Insets(inset, inset, inset, inset);
            contentPanel.setLayout(layout);

            /*
             * All components for information
             */
            c.gridx = 0;
            c.gridy = 0;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.fill = GridBagConstraints.NONE;
            contentPanel.add(
                    buildSvgIcon("/resources/icon/statistics_information.svg"),
                    c);

            c.gridx = 1;
            c.gridy = 0;
            c.gridheight = 1;
            c.gridwidth = 2;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.BOTH;
            JLabel infoLabel = new JLabel(
                    Messages.getString("StatisticsViewDialog.Information"));
            infoLabel.setFont(FontFactory.createLcdFont());
            contentPanel.add(infoLabel, c);

            c.gridx = 1;
            c.gridy = 1;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel
                    .add(new JLabel(Messages
                            .getString("StatisticsViewDialog.NonogramName")), c);

            c.gridx = 2;
            c.gridy = 1;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(new JLabel(stats.getValue("nonogramName")), c);

            c.gridx = 1;
            c.gridy = 2;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(
                    new JLabel(Messages
                            .getString("StatisticsViewDialog.Course")), c);

            c.gridx = 2;
            c.gridy = 2;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(new JLabel(stats.getValue("course")), c);

            /*
             * All components for time
             */
            c.gridx = 0;
            c.gridy = 4;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.fill = GridBagConstraints.NONE;
            contentPanel.add(
                    buildSvgIcon("/resources/icon/statistics_time.svg"), c);

            c.gridx = 1;
            c.gridy = 4;
            c.gridheight = 1;
            c.gridwidth = 2;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.BOTH;
            JLabel timeLabel = new JLabel(
                    Messages.getString("StatisticsViewDialog.Time"));
            timeLabel.setFont(FontFactory.createLcdFont());
            contentPanel.add(timeLabel, c);

            c.gridx = 1;
            c.gridy = 5;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(
                    new JLabel(Messages
                            .getString("StatisticsViewDialog.GameTime")), c);

            c.gridx = 2;
            c.gridy = 5;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(new JLabel(stats.getValue("gameTime")), c);

            c.gridx = 1;
            c.gridy = 6;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(
                    new JLabel(Messages
                            .getString("StatisticsViewDialog.PauseTime")), c);

            c.gridx = 2;
            c.gridy = 6;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(new JLabel(stats.getValue("pauseTime")), c);

            /*
             * All components for performance
             */
            c.gridx = 0;
            c.gridy = 8;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.fill = GridBagConstraints.NONE;
            contentPanel.add(
                    buildSvgIcon("/resources/icon/statistics_performance.svg"),
                    c);

            c.gridx = 1;
            c.gridy = 8;
            c.gridheight = 1;
            c.gridwidth = 2;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.BOTH;
            JLabel performanceLabel = new JLabel(
                    Messages.getString("StatisticsViewDialog.Performance"));
            performanceLabel.setFont(FontFactory.createLcdFont());
            contentPanel.add(performanceLabel, c);

            c.gridx = 1;
            c.gridy = 9;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel
                    .add(new JLabel(
                            Messages.getString("StatisticsViewDialog.OccupyPerformance")),
                            c);

            c.gridx = 2;
            c.gridy = 9;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel
                    .add(new JLabel(stats.getValue("occupyPerformance")), c);

            c.gridx = 1;
            c.gridy = 10;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel
                    .add(new JLabel(
                            Messages.getString("StatisticsViewDialog.MarkingPerformance")),
                            c);

            c.gridx = 2;
            c.gridy = 10;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(new JLabel(stats.getValue("markPerformance")), c);

            c.gridx = 0;
            c.gridy = 12;
            c.gridheight = 1;
            c.gridwidth = 3;
            c.anchor = GridBagConstraints.SOUTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(buildButtonPane(), c);
        }

        return contentPanel;
    }

    /**
     * Gets icon from svg file and builds a panel to display it.
     * 
     * @param resourceName
     *            String containing the resources (svg file) name.
     * @return Panel with svg image.
     */
    private JPanel buildSvgIcon(final String resourceName) {

        SVGPanel panel = new SVGPanel();

        try {

            panel.setSvgURI(getClass().getResource(resourceName).toURI());
            panel.setAntiAlias(true);
            panel.setPreferredSize(new Dimension(SVG_WIDTH, SVG_HEIGHT));
            panel.setScaleToFit(true);
            panel.setOpaque(false);
            panel.repaint();

        } catch (URISyntaxException e) {

            logger.debug("Could not open image file for statistics dialog.");
        }

        return panel;

        // URI image = universe.loadSVG(getClass().getResource(
        // "/resources/icon/statistics_performance.svg"));
        // SVGDiagram diagram = universe.getDiagram(image);
        // SVGDisplayPanel panel = new SVGDisplayPanel();
        // panel.setDiagram(diagram);
        // panel.setOpaque(false);
        // panel.setPreferredSize(new Dimension(50, 50));
        //
        // return panel;
    }

    /**
     * Builds a panel with all necessary buttons including their action
     * listeners.
     * 
     * @return Panel with all buttons.
     */
    private JPanel buildButtonPane() {

        JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BorderLayout());

        JButton okButton = new JButton(
                Messages.getString("StatisticsViewDialog.OK"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {

                dispose();
            }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton, BorderLayout.EAST);
        getRootPane().setDefaultButton(okButton);

        return buttonPane;
    }
}

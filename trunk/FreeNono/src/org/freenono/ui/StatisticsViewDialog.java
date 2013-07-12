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
import org.freenono.controller.SimpleStatistics;
import org.freenono.ui.common.FontFactory;

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

    private GridBagLayout layout;
    private GridBagConstraints c;

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

            // Set layout and constraints
            final int inset = 10;
            layout = new GridBagLayout();
            c = new GridBagConstraints();
            c.insets = new Insets(inset, inset, inset, inset);
            contentPanel.setLayout(layout);

            int currentRow = 0;

            /*
             * All components for information
             */
            buildCaption(contentPanel, currentRow,
                    "/resources/icon/statistics_information.svg",
                    Messages.getString("StatisticsViewDialog.Information"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow,
                    Messages.getString("StatisticsViewDialog.NonogramName"),
                    stats.getValue("nonogramName"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow,
                    Messages.getString("StatisticsViewDialog.Course"),
                    stats.getValue("course"));

            currentRow += 1;

            /*
             * All components for time
             */
            buildCaption(contentPanel, currentRow,
                    "/resources/icon/statistics_time.svg",
                    Messages.getString("StatisticsViewDialog.Time"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow,
                    Messages.getString("StatisticsViewDialog.GameTime"),
                    stats.getValue("gameTime"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow,
                    Messages.getString("StatisticsViewDialog.PauseTime"),
                    stats.getValue("pauseTime"));

            currentRow += 1;

            /*
             * All components for performance
             */
            buildCaption(contentPanel, currentRow,
                    "/resources/icon/statistics_performance.svg",
                    Messages.getString("StatisticsViewDialog.Performance"));

            currentRow += 1;

            buildInformation(
                    contentPanel,
                    currentRow,
                    Messages.getString("StatisticsViewDialog.OccupyPerformance"),
                    stats.getValue("occupyPerformance"));

            currentRow += 1;

            buildInformation(
                    contentPanel,
                    currentRow,
                    Messages.getString("StatisticsViewDialog.MarkingPerformance"),
                    stats.getValue("markPerformance"));

            currentRow += 1;

            c.gridx = 0;
            c.gridy = currentRow;
            c.gridheight = 1;
            c.gridwidth = 3;
            c.anchor = GridBagConstraints.SOUTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(buildButtonPane(), c);
        }

        return contentPanel;
    }

    /**
     * Sets constraints for caption image and its label and adds them to content
     * panel.
     * 
     * @param contentPane
     *            content pane to add caption to
     * @param row
     *            row to include caption image in
     * @param svgIconFile
     *            resource path to svg image file
     * @param captionText
     *            text to show next to svg image
     */
    private void buildCaption(final JPanel contentPane, final int row,
            final String svgIconFile, final String captionText) {

        c.gridx = 0;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        contentPane.add(buildSvgIcon(svgIconFile), c);

        c.gridx = 1;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        JLabel captionInfoLabel = new JLabel(captionText);
        captionInfoLabel.setFont(FontFactory.createLcdFont());
        contentPane.add(captionInfoLabel, c);
    }

    /**
     * Sets constraints for information label and its value adds them to content
     * panel.
     * 
     * @param contentPane
     *            content pane to add caption to
     * @param row
     *            row to include caption image in
     * @param labelText
     *            text label describing statistical information
     * @param valueText
     *            value for statistical information
     */
    private void buildInformation(final JPanel contentPane, final int row,
            final String labelText, final String valueText) {

        c.gridx = 1;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(new JLabel(labelText), c);

        c.gridx = 2;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(new JLabel(valueText), c);
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
            @Override
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

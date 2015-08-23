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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.controller.SimpleStatistics;
import org.freenono.ui.common.FontFactory;
import org.freenono.ui.common.FreeNonoDialog;

/**
 * Shows a statistics dialog with information concerning the current or last played game.
 *
 * @author Christian Wichmann
 */
public class StatisticsViewDialog extends FreeNonoDialog {

    private static final long serialVersionUID = -185463984939167375L;

    private static Logger logger = Logger.getLogger(StatisticsViewDialog.class);

    private GridBagLayout layout;
    private GridBagConstraints c;

    private JPanel contentPanel = null;
    private final Settings settings;

    private final DecimalFormat formatter = new DecimalFormat("0.0");

    /**
     * Initializes a new dialog to view statistics.
     *
     * @param owner
     *            parent frame of this dialog
     *
     * @param settings
     *            Settings object for background color.
     */
    public StatisticsViewDialog(final Frame owner, final Settings settings) {

        super(owner, settings.getTextColor(), settings.getColorModel().getTopColor());

        this.settings = settings;

        initialize();

        addKeyBindings();
    }

    /**
     * Initializes this StatisticsViewDialog.
     */
    private void initialize() {

        setTitle(Messages.getString("StatisticsViewDialog.Title"));
        setForeground(settings.getTextColor());
        getContentPane().add(buildContentPane());

        pack();
    }

    /**
     * Builds a panel to hold all components.
     *
     * @return Content panel.
     */
    private JPanel buildContentPane() {

        if (contentPanel == null) {

            logger.debug("Building statistics panel...");

            final SimpleStatistics stats = SimpleStatistics.getInstance();

            contentPanel = new JPanel();

            // Set layout and constraints
            final int inset = 10;
            layout = new GridBagLayout();
            c = new GridBagConstraints();
            c.insets = new Insets(inset, inset, inset, inset);
            contentPanel.setLayout(layout);
            contentPanel.setBackground(settings.getColorModel().getTopColor());
            contentPanel.setForeground(settings.getColorModel().getBottomColor());

            int currentRow = 0;

            /*
             * All components for information
             */
            buildCaption(contentPanel, currentRow, "/resources/icon/statistics_information.png",
                    Messages.getString("StatisticsViewDialog.Information"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow, Messages.getString("StatisticsViewDialog.NonogramName"),
                    (String) stats.getValue("nonogramName"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow, Messages.getString("StatisticsViewDialog.Difficulty"),
                    (String) stats.getValue("nonogramDifficulty"));

            currentRow += 1;

            /*
             * All components for time
             */
            buildCaption(contentPanel, currentRow, "/resources/icon/statistics_time.png", Messages.getString("StatisticsViewDialog.Time"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow, Messages.getString("StatisticsViewDialog.GameTime"),
                    (String) stats.getValue("gameTime"));

            currentRow += 1;

            buildInformation(contentPanel, currentRow, Messages.getString("StatisticsViewDialog.PauseTime"),
                    (String) stats.getValue("pauseTime"));

            currentRow += 1;

            /*
             * All components for performance
             */
            buildCaption(contentPanel, currentRow, "/resources/icon/statistics_performance.png",
                    Messages.getString("StatisticsViewDialog.Performance"));

            currentRow += 1;

            String performance =
                    formatter.format(stats.getValue("occupyPerformance")) + " " + Messages.getString("SimpleStatistics.FieldsPerMinute");
            buildInformation(contentPanel, currentRow, Messages.getString("StatisticsViewDialog.OccupyPerformance"), performance);

            currentRow += 1;

            performance =
                    formatter.format(stats.getValue("markPerformance")) + " " + Messages.getString("SimpleStatistics.FieldsPerMinute");
            buildInformation(contentPanel, currentRow, Messages.getString("StatisticsViewDialog.MarkingPerformance"), performance);

            currentRow += 1;

            c.gridx = 0;
            c.gridy = currentRow;
            c.gridheight = 1;
            c.gridwidth = 3;
            c.anchor = GridBagConstraints.SOUTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPanel.add(buildButtonPane(), c);

            logger.debug("Build statistics panel.");
        }

        return contentPanel;
    }

    /**
     * Sets constraints for caption image and its label and adds them to content panel.
     *
     * @param contentPane
     *            content pane to add caption to
     * @param row
     *            row to include caption image in
     * @param captionIconFile
     *            resource path to svg image file
     * @param captionText
     *            text to show next to svg image
     */
    private void buildCaption(final JPanel contentPane, final int row, final String captionIconFile, final String captionText) {

        c.gridx = 0;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        contentPane.add(new JLabel(new ImageIcon(getClass().getResource(captionIconFile))), c);

        c.gridx = 1;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        final JLabel captionInfoLabel = new JLabel(captionText);
        captionInfoLabel.setFont(FontFactory.createLcdFont());
        contentPane.add(captionInfoLabel, c);
    }

    /**
     * Sets constraints for information label and its value adds them to content panel.
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
    private void buildInformation(final JPanel contentPane, final int row, final String labelText, final String valueText) {

        c.gridx = 1;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        final JLabel informationLabel = new JLabel(labelText);
        informationLabel.setFont(FontFactory.createTextFont());
        contentPanel.add(informationLabel, c);

        c.gridx = 2;
        c.gridy = row;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        final JLabel informationContent = new JLabel(valueText);
        informationContent.setFont(FontFactory.createTextFont());
        contentPanel.add(informationContent, c);
    }

    /**
     * Builds a panel with all necessary buttons including their action listeners.
     *
     * @return Panel with all buttons.
     */
    private JPanel buildButtonPane() {

        final JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BorderLayout());

        final JButton okButton = new JButton(Messages.getString("StatisticsViewDialog.OK"));
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

    /**
     * Adds key bindings for this dialog to exit it.
     */
    private void addKeyBindings() {

        final JComponent rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "QuitStatisticsViewDialog");
        rootPane.getActionMap().put("QuitStatisticsViewDialog", new AbstractAction() {

            private static final long serialVersionUID = 8132652822791902496L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });
    }
}

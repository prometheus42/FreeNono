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
package org.freenono.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.controller.achievements.Achievement;
import org.freenono.controller.achievements.AchievementManager;
import org.freenono.ui.common.FontFactory;
import org.freenono.ui.common.FreeNonoDialog;

/**
 * Shows the dialog to show all achievements and the information if and when
 * they were accomplished. Each achievement is displayed with a icon image and
 * its explanation text. By using the reset button all achievements that have
 * been accomplished can be reset.
 * <p>
 * This dialog is usable as overview of all achievement and their status or to
 * show only some achievement that have been accomplished with the last game for
 * example. Each use has its own constructor. If a change set as Map object is
 * given only those achievements in the change set will be shown.
 * 
 * @author Christian Wichmann
 */
public class AchievementDialog extends FreeNonoDialog {

    private static final long serialVersionUID = -1759435182362182780L;

    private static Logger logger = Logger.getLogger(GameOverUI.class);

    private Settings settings = null;

    private JPanel contentPane = null;
    private JButton closeButton = null;
    private JButton resetButton = null;
    private JLabel titleLabel = null;

    private final String dialogTitle;
    private Map<Achievement, Boolean> changedAchievements;
    private Map<Achievement, JLabel> listOfAchievementLabels;
    private Map<Achievement, JLabel> listOfAchievementIcons;

    /**
     * Initializes a dialog to show all achievements and the information if and
     * when they were accomplished.
     * 
     * @param owner
     *            frame that owns this dialog
     * @param settings
     *            Settings object for color options.
     */
    public AchievementDialog(final Frame owner, final Settings settings) {

        // TODO Find a better way than two constructors with nearly the exact
        // code!

        super(owner, settings.getColorModel().getBottomColor(), settings.getColorModel().getTopColor());

        this.settings = settings;

        changedAchievements = Collections.emptyMap();
        listOfAchievementLabels = new HashMap<Achievement, JLabel>();
        listOfAchievementIcons = new HashMap<Achievement, JLabel>();

        dialogTitle = Messages.getString("AchievementDialog.Title");

        initialize();

        updateAchievementLabels();

        addKeyBindings();

        closeButton.grabFocus();
    }

    /**
     * Initializes a dialog to show all achievements and the information if and
     * when they were accomplished.
     * 
     * @param owner
     *            frame that owns this dialog
     * @param settings
     *            settings object for color options
     * @param changes
     *            map with the change set of achievements that should be shown
     */
    public AchievementDialog(final Frame owner, final Settings settings, final Map<Achievement, Boolean> changes) {

        super(owner, settings.getColorModel().getBottomColor(), settings.getColorModel().getTopColor());

        this.settings = settings;

        changedAchievements = changes;
        listOfAchievementLabels = new HashMap<Achievement, JLabel>();
        listOfAchievementIcons = new HashMap<Achievement, JLabel>();

        dialogTitle = Messages.getString("AchievementDialog.TitleChange");

        initialize();

        updateAchievementLabels();

        addKeyBindings();

        closeButton.grabFocus();
    }

    /**
     * Initializes dialog to show all achievements and the information if and
     * when they were accomplished.
     */
    private void initialize() {

        getContentPane().add(buildContentPane());

        pack();
    }

    /**
     * Initializes the content pane for this dialog.
     * 
     * @return content pane with all components
     */
    private JPanel buildContentPane() {

        if (contentPane == null) {

            final int inset = 10;
            final float titleFontSize = 22;

            contentPane = new JPanel();
            contentPane.setBackground(settings.getColorModel().getTopColor());
            contentPane.setForeground(settings.getColorModel().getBottomColor());

            final GridBagLayout layout = new GridBagLayout();
            contentPane.setLayout(layout);
            final GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(inset, inset, inset, inset);

            // add title label
            titleLabel = new JLabel();
            titleLabel.setText("<html><p style=\"text-align:center;\">" + dialogTitle + "</p></html>");
            titleLabel.setFont(FontFactory.createTextFont().deriveFont(titleFontSize));
            c.gridx = 0;
            c.gridy = 0;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            contentPane.add(titleLabel, c);

            // add panel with all achievement icons
            c.gridx = 0;
            c.gridy = 1;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.BOTH;
            contentPane.add(buildAchievementPane(), c);

            // add success information when all achievements were accomplished
            if (AchievementManager.getInstance().areAllAchievementAccomplished()) {
                JLabel successLabel = new JLabel();
                successLabel.setText("<html><p style=\"text-align:center;\">" + Messages.getString("AchievementDialog.Success")
                        + "</p></html>");
                successLabel.setFont(FontFactory.createTextFont().deriveFont(titleFontSize));
                successLabel.setForeground(Color.RED);
                c.gridx = 0;
                c.gridy = 2;
                c.gridheight = 1;
                c.gridwidth = 1;
                c.anchor = GridBagConstraints.CENTER;
                c.fill = GridBagConstraints.NONE;
                contentPane.add(successLabel, c);
            }

            // add close button for dialog
            c.gridx = 0;
            c.gridy = 3;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.SOUTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            contentPane.add(buildButtonPane(), c);
        }

        assert contentPane != null;

        return contentPane;
    }

    /**
     * Creates achievement icons if some there criteria are met at the last
     * played game. The currently supported and displayed achievements are
     * defined in the enum <code>Achievement</code>.
     * <p>
     * @return pane containing all accomplished achievements
     */
    private Component buildAchievementPane() {

        final int inset = 5;
        final float achievementExplanationFontSize = 18;
        final GridBagLayout layout = new GridBagLayout();
        final JPanel achievementPane = new JPanel(layout);
        final GridBagConstraints c = new GridBagConstraints();
        int currentRow = 0;
        c.insets = new Insets(inset, inset, inset, inset);
        achievementPane.setOpaque(false);

        // map all achievements to their icons for this dialog
        Map<Achievement, String> icons = new HashMap<>();
        icons.put(Achievement.HIGH_SPEED_SOLVING, "/resources/icon/achievement_1.png");
        icons.put(Achievement.VERY_HIGH_SPEED_SOLVING, "/resources/icon/achievement_2.png");
        icons.put(Achievement.ULTRA_HIGH_SPEED_SOLVING, "/resources/icon/achievement_3.png");
        icons.put(Achievement.ONE_WITHOUT_ERROR, "/resources/icon/achievement_4.png");
        icons.put(Achievement.THREE_WITHOUT_ERROR, "/resources/icon/achievement_5.png");
        icons.put(Achievement.FIVE_WITHOUT_ERROR, "/resources/icon/achievement_6.png");
        icons.put(Achievement.COURSE_COMPLETED, "/resources/icon/achievement_7.png");
        icons.put(Achievement.UNMARKED, "/resources/icon/achievement_7.png");

        // build components for all achievements that should be displayed
        for (Achievement achievement : Achievement.values()) {

            if (changedAchievements.isEmpty() || changedAchievements.containsKey(achievement)) {

                // build and add image icon for achievement
                ImageIcon image1 = new ImageIcon(getClass().getResource(icons.get(achievement)));
                JLabel achievementLabel = new JLabel(image1);
                achievementLabel.setToolTipText(achievement.toString());
                c.gridx = 0;
                c.gridy = currentRow;
                c.gridheight = 1;
                c.gridwidth = 1;
                c.anchor = GridBagConstraints.CENTER;
                c.fill = GridBagConstraints.NONE;
                achievementPane.add(achievementLabel, c);
                listOfAchievementIcons.put(achievement, achievementLabel);

                // build and add text label with explanation
                JLabel achievementText = new JLabel();
                achievementText.setFont(FontFactory.createTextFont().deriveFont(achievementExplanationFontSize));
                achievementText.setText("<html><body style='width: 350px'>" + achievement.toString() + "</html>");
                c.gridx = 1;
                c.gridy = currentRow++;
                c.gridheight = 1;
                c.gridwidth = 1;
                c.anchor = GridBagConstraints.WEST;
                c.fill = GridBagConstraints.NONE;
                achievementPane.add(achievementText, c);
                listOfAchievementLabels.put(achievement, achievementText);
            }
        }

        assert achievementPane != null : "Achievement Pane should not be Null!";
        return achievementPane;
    }

    /**
     * Updates all labels and icons for the achievements according to their
     * status in AchievementManager.
     */
    private void updateAchievementLabels() {

        for (Achievement achievement : Achievement.values()) {
            // update only when achievement is displayed in this dialog
            if (listOfAchievementIcons.containsKey(achievement)) {
                // check whether achievement was already accomplished
                boolean accomplished = AchievementManager.getInstance().isAchievementAccomplished(achievement);
                // set icon label and explanation text
                if (accomplished) {
                    listOfAchievementIcons.get(achievement).setEnabled(true);
                    listOfAchievementLabels.get(achievement).setEnabled(true);
                } else {
                    listOfAchievementIcons.get(achievement).setEnabled(false);
                    listOfAchievementLabels.get(achievement).setEnabled(false);
                }
            }
        }
    }

    /**
     * Initializes the close button for this dialog.
     * 
     * @return Close button for this dialog.
     */
    private JPanel buildButtonPane() {

        JPanel buttonPane = new JPanel(new BorderLayout());
        buttonPane.setOpaque(false);

        if (closeButton == null) {

            closeButton = new JButton();
            closeButton.setText(Messages.getString("GameOverUI.CloseButton"));
            getRootPane().setDefaultButton(closeButton);
            closeButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    handleExit();
                }
            });
        }

        if (resetButton == null) {

            resetButton = new JButton();
            resetButton.setText(Messages.getString("AchievementDialog.ResetButtonTitle"));
            getRootPane().setDefaultButton(closeButton);
            resetButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    AchievementManager.getInstance().resetAllAchievements();
                    updateAchievementLabels();
                }
            });
        }

        buttonPane.add(resetButton, BorderLayout.WEST);
        buttonPane.add(closeButton, BorderLayout.EAST);

        return buttonPane;
    }

    /**
     * Adds key bindings for this dialog to exit it.
     */
    private void addKeyBindings() {

        final JComponent rootPane = getRootPane();

        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "QuitAchievementDialog");
        rootPane.getActionMap().put("QuitAchievementDialog", new AbstractAction() {

            private static final long serialVersionUID = 653149778238948695L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                handleExit();
            }
        });
    }

    /**
     * Handles exiting this dialog.
     */
    private void handleExit() {

        logger.debug("Closing achievements dialog...");
        setVisible(false);
        dispose();
    }
}

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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.provider.NonogramProvider;
import org.freenono.ui.common.FontFactory;
import org.freenono.ui.common.FreeNonoDialog;
import org.freenono.ui.explorer.NonogramButton;
import org.freenono.ui.highscore.HighscoreTable;

/**
 * Shows the dialog at the end of a game.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public class GameOverUI extends FreeNonoDialog {

    private static final long serialVersionUID = -1759435182362182780L;

    private static Logger logger = Logger.getLogger(GameOverUI.class);

    private NonogramProvider pattern = null;
    private NonogramProvider nextNonogramToPlay = null;
    private boolean isSolved = false;
    private Settings settings = null;

    private JPanel contentPane = null;
    private JButton closeButton = null;
    private JLabel messageLabel = null;

    private NonogramButton currentNonogramButton;
    private NonogramButton previousNonogramButton;
    private NonogramButton nextNonogramButton;

    /**
     * Initializes a dialog to mark the end of game. Shown information depends
     * on whether the game was won or lost. After building gui components and
     * adding listeners this dialog will show itself automatically.
     * 
     * @param owner
     *            frame that owns this dialog
     * @param pattern
     *            Nonogram that was played before.
     * @param isSolved
     *            If game was won or lost.
     * @param settings
     *            Settings object for color options.
     */
    public GameOverUI(final Frame owner, final NonogramProvider pattern, final boolean isSolved, final Settings settings) {

        super(owner, settings.getColorModel().getBottomColor(), settings.getColorModel().getTopColor());

        if (pattern == null || settings == null) {
            throw new NullPointerException("At least one argument is not valid.");
        }

        this.pattern = pattern;
        this.isSolved = isSolved;
        this.settings = settings;

        initialize();

        addListener();

        addKeyBindings();

        closeButton.grabFocus();
    }

    /**
     * Initializes dialog at the end of the game depending on whether the game
     * was won or lost.
     */
    private void initialize() {

        getContentPane().add(buildContentPane());
        pack();
    }

    /**
     * Initializes the content pane for this dialog. Next and previous nonograms
     * are shown as <code>NonogramButton</code> components.
     * 
     * @return content pane with all components
     */
    private JPanel buildContentPane() {

        if (contentPane == null) {

            final int inset = 20;
            final float messageFontSize = 18;

            contentPane = new JPanel();
            contentPane.setBackground(settings.getColorModel().getTopColor());
            contentPane.setForeground(settings.getColorModel().getBottomColor());

            final GridBagLayout layout = new GridBagLayout();
            contentPane.setLayout(layout);
            final GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(inset, inset, inset, inset);
            int currentRow = 0;

            /*
             * Create label for message depending on whether game was won or
             * lost.
             */
            messageLabel = new JLabel();
            if (isSolved) {
                messageLabel.setText("<html><p style=\"text-align:center;\">" + Messages.getString("GameOverUI.WinningText")
                        + "</p></html>");
            } else {
                messageLabel
                        .setText("<html><p style=\"text-align:center;\">" + Messages.getString("GameOverUI.LosingText") + "</p></html>");
            }
            messageLabel.setFont(FontFactory.createTextFont().deriveFont(messageFontSize));
            c.gridx = 0;
            c.gridy = currentRow++;
            c.gridheight = 1;
            c.gridwidth = 5;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            contentPane.add(messageLabel, c);

            /*
             * Create separate panel for all nonogram buttons for current
             * nonogram in course and the next/previous nonogram.
             */
            c.gridx = 0;
            c.gridy = currentRow++;
            c.gridheight = 1;
            c.gridwidth = 5;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            contentPane.add(buildNonogramButtons(inset), c);

            /*
             * Create name of nonogram when game was won or user chose to see
             * it.
             */
            currentRow++;
            if (isSolved || settings.isShowNonogramName()) {

                c.gridx = 4;
                c.gridy = currentRow++;
                c.gridheight = 1;
                c.gridwidth = 1;
                c.anchor = GridBagConstraints.CENTER;
                c.fill = GridBagConstraints.NONE;
                final JLabel nonogramNameLabel = new JLabel(pattern.getName());
                nonogramNameLabel.setFont(FontFactory.createLcdFont());
                contentPane.add(nonogramNameLabel, c);
            }

            /*
             * Create scroll pane including a high score table.
             */
            c.gridx = 0;
            c.gridy = currentRow++;
            c.gridheight = 1;
            c.gridwidth = 5;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            final HighscoreTable hst = new HighscoreTable(settings, pattern.fetchNonogram());
            final JScrollPane sp = new JScrollPane(hst, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            sp.getViewport().setBackground(settings.getColorModel().getTopColor());
            sp.setBackground(settings.getColorModel().getTopColor());
            final int highscoreWidth = 400;
            final int highscoreHeight = 100;
            sp.setPreferredSize(new Dimension(highscoreWidth, highscoreHeight));
            contentPane.add(sp, c);

            /*
             * Create button to close dialog if user does not want to play
             * anymore.
             */
            c.gridx = 4;
            c.gridy = currentRow++;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            contentPane.add(buildCloseButton(), c);
        }

        assert contentPane != null;

        return contentPane;
    }

    /**
     * Builds a panel for all nonogram buttons including the last played and the
     * next/previous nonogram in the course.
     * 
     * @param inset
     *            inset for the GridbagLayout of this panel
     * @return panel with all nonogram buttons
     */
    private JPanel buildNonogramButtons(final int inset) {

        final float arrowFontSize = 24;

        JPanel nonogramButtonPane = new JPanel();
        nonogramButtonPane.setBackground(settings.getColorModel().getTopColor());
        nonogramButtonPane.setForeground(settings.getColorModel().getBottomColor());

        final GridBagLayout layout = new GridBagLayout();
        nonogramButtonPane.setLayout(layout);
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(inset, inset, inset, inset);

        // Create nonogram buttons for current nonogram.
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        currentNonogramButton = new NonogramButton(pattern);
        nonogramButtonPane.add(currentNonogramButton, c);

        // Create nonogram button for previous nonogram.
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        final NonogramProvider previous = pattern.getPreviousNonogram();
        if (previous != null) {
            previousNonogramButton = new NonogramButton(previous);
            nonogramButtonPane.add(previousNonogramButton, c);
        } else {
            final JLabel placeHolder = new JLabel();
            placeHolder.setPreferredSize(new Dimension(75, 75));
            nonogramButtonPane.add(placeHolder, c);
        }
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        final JLabel arrowLeft = (previous != null) ? new JLabel("\u2190") : new JLabel("   ");
        arrowLeft.setFont(FontFactory.createTextFont().deriveFont(arrowFontSize));
        nonogramButtonPane.add(arrowLeft, c);

        // Create nonogram button for next nonogram.
        c.gridx = 4;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        final NonogramProvider next = pattern.getNextNonogram();
        if (next != null) {
            nextNonogramButton = new NonogramButton(next);
            nonogramButtonPane.add(nextNonogramButton, c);
        } else {
            final JLabel placeHolder = new JLabel();
            placeHolder.setPreferredSize(new Dimension(75, 75));
            nonogramButtonPane.add(placeHolder, c);
        }
        c.gridx = 3;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        final JLabel arrowRight = (next != null) ? new JLabel("\u2192") : new JLabel("   ");
        arrowRight.setFont(FontFactory.createTextFont().deriveFont(arrowFontSize));
        nonogramButtonPane.add(arrowRight, c);

        return nonogramButtonPane;
    }

    /**
     * Initializes the close button for this dialog.
     * 
     * @return Close button for this dialog.
     */
    private JButton buildCloseButton() {

        if (closeButton == null) {

            closeButton = new JButton();
            closeButton.setText(Messages.getString("GameOverUI.CloseButton"));
            getRootPane().setDefaultButton(closeButton);
            closeButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    setVisible(false);
                    dispose();
                }
            });
        }
        return closeButton;
    }

    /**
     * Adds listener for handling nonogram buttons.
     */
    private void addListener() {

        logger.debug("Adding listeners for GameOverUI...");

        currentNonogramButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                nextNonogramToPlay = pattern;
                dispose();
            }
        });

        if (previousNonogramButton != null) {

            previousNonogramButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    nextNonogramToPlay = pattern.getPreviousNonogram();
                    dispose();
                }
            });
        }

        if (nextNonogramButton != null) {
            nextNonogramButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    nextNonogramToPlay = pattern.getNextNonogram();
                    dispose();
                }
            });
        }
    }

    /**
     * Adds key bindings for this dialog to exit it.
     */
    private void addKeyBindings() {

        final JComponent rootPane = getRootPane();

        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "QuitGameOverDialog");
        rootPane.getActionMap().put("QuitGameOverDialog", new AbstractAction() {

            private static final long serialVersionUID = 653149778238948695L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
    }

    /**
     * Gets next nonogram that player wants to play.
     * 
     * @return next nonogram to be played or null if no nonogram was chosen
     */
    public final NonogramProvider getNextNonogramToPlay() {

        return nextNonogramToPlay;
    }
}

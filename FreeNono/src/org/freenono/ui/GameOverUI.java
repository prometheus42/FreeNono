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
import java.awt.Insets;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.interfaces.NonogramProvider;

/**
 * Shows the dialog at the end of a game.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public class GameOverUI extends JDialog {

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
     * @param pattern
     *            Nonogram that was played before.
     * @param isSolved
     *            If game was won or lost.
     * @param settings
     *            Settings object for color options.
     */
    public GameOverUI(final NonogramProvider pattern, final boolean isSolved,
            final Settings settings) {

        // TODO give parent for JDialog?
        super();
        
        if (pattern == null || settings == null) {
            throw new IllegalArgumentException("At least one argument is not valid.");
        }

        this.pattern = pattern;
        this.isSolved = isSolved;
        this.settings = settings;

        initialize();

        addListener();

        addKeyBindings();
        
        closeButton.grabFocus();
        
        setVisible(true);
    }

    /**
     * Initializes dialog at the end of the game depending on whether the game
     * was won or lost.
     */
    private void initialize() {

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(Messages.getString("GameOverUI.Title"));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setUndecorated(true);

        getContentPane().add(buildContentPane());

        // pack dialog and set location to center of screen
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Initializes the content pane for this dialog.
     * 
     * @return content pane with all elements
     */
    private JPanel buildContentPane() {

        if (contentPane == null) {

            final int inset = 20;
            final float messageFontSize = 18;
            final float arrowFontSize = 24;

            contentPane = new JPanel();

            contentPane.setBackground(settings.getColorModel().getTopColor());
            contentPane
                    .setForeground(settings.getColorModel().getBottomColor());
            contentPane.setBorder(BorderFactory.createEtchedBorder());

            GridBagLayout layout = new GridBagLayout();
            contentPane.setLayout(layout);

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(inset, inset, inset, inset);
            int currentRow = 0;

            /*
             * Create label for message depending on whether game was won or
             * lost.
             */
            messageLabel = new JLabel();
            if (isSolved) {
                messageLabel.setText("<html><p style=\"text-align:center;\">"
                        + Messages.getString("GameOverUI.WinningText")
                        + "</p></html>");
            } else {
                messageLabel.setText("<html><p style=\"text-align:center;\">"
                        + Messages.getString("GameOverUI.LosingText")
                        + "</p></html>");
            }
            messageLabel.setFont(FontFactory.createTextFont().deriveFont(
                    messageFontSize));
            c.gridx = 0;
            c.gridy = currentRow++;
            c.gridheight = 1;
            c.gridwidth = 5;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            contentPane.add(messageLabel, c);

            // Create nonogram buttons for current nonogram.
            c.gridx = 2;
            c.gridy = currentRow;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            currentNonogramButton = new NonogramButton(pattern);
            contentPane.add(currentNonogramButton, c);

            // Create nonogram button for previous nonogram.
            c.gridx = 0;
            c.gridy = currentRow;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            NonogramProvider previous = pattern.getPreviousNonogram();
            if (previous != null) {
                previousNonogramButton = new NonogramButton(previous);
                contentPane.add(previousNonogramButton, c);
            } else {
                JLabel placeHolder = new JLabel();
                placeHolder.setPreferredSize(new Dimension(75, 75));
                contentPane.add(placeHolder, c);
            }
            c.gridx = 1;
            c.gridy = currentRow;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            JLabel arrowLeft = (previous != null) ? new JLabel("\u2190")
                    : new JLabel("   ");
            arrowLeft.setFont(FontFactory.createTextFont().deriveFont(
                    arrowFontSize));
            contentPane.add(arrowLeft, c);

            // Create nonogram button for next nonogram.
            c.gridx = 4;
            c.gridy = currentRow;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            NonogramProvider next = pattern.getNextNonogram();
            if (next != null) {
                nextNonogramButton = new NonogramButton(next);
                contentPane.add(nextNonogramButton, c);
            } else {
                JLabel placeHolder = new JLabel();
                placeHolder.setPreferredSize(new Dimension(75, 75));
                contentPane.add(placeHolder, c);
            }
            c.gridx = 3;
            c.gridy = currentRow;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            JLabel arrowRight = (next != null) ? new JLabel("\u2192")
                    : new JLabel("   ");
            arrowRight.setFont(FontFactory.createTextFont().deriveFont(
                    arrowFontSize));
            contentPane.add(arrowRight, c);

            /*
             * Create name of nonogram when game was won or user chose to see
             * it.
             */
            currentRow++;
            if (isSolved || settings.isShowNonogramName()) {

                c.gridx = 1;
                c.gridy = currentRow++;
                c.gridheight = 1;
                c.gridwidth = 3;
                c.anchor = GridBagConstraints.CENTER;
                c.fill = GridBagConstraints.NONE;
                JLabel nonogramNameLabel = new JLabel(pattern.getName());
                nonogramNameLabel.setFont(FontFactory.createLcdFont());
                contentPane.add(nonogramNameLabel, c);
            }

            /*
             * Create button to close dialog if user does not want to play
             * anymore.
             */
            c.gridx = 2;
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

            public void actionPerformed(final ActionEvent e) {

                nextNonogramToPlay = pattern;
                dispose();
            }
        });

        if (previousNonogramButton != null) {
            
            previousNonogramButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {

                    nextNonogramToPlay = pattern.getPreviousNonogram();
                    dispose();
                }
            });
        }

        if (nextNonogramButton != null) {
            nextNonogramButton.addActionListener(new ActionListener() {

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

        JComponent rootPane = getRootPane();

        // TODO fix key binding to close dialog with escape
        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "QuitGameOverDialog");
        rootPane.getActionMap().put("QuitGameOverDialog", new AbstractAction() {

            private static final long serialVersionUID = 653149778238948695L;

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

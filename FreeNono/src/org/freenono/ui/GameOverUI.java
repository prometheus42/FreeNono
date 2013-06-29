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

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;

import org.freenono.board.BoardPreview;
import org.freenono.controller.Settings;
import org.freenono.interfaces.NonogramProvider;

/**
 * Shows the dialog at the end of a game.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public class GameOverUI extends JDialog {

    private static final long serialVersionUID = 1L;

    private NonogramProvider pattern = null;
    private boolean isSolved = false;
    private Settings settings = null;

    private JPanel jContentPane = null;
    private JLabel nonogramNameLabel = null;
    private JButton closeButton = null;
    private JLabel messageLabel = null;

    private BoardPreview boardPreview = null;

    /**
     * Initializes a dialog to mark the end of game. Shown information depends
     * on whether the game was won or lost.
     * 
     * @param pattern
     *            Nonogram that was played before.
     * @param boardPreview
     *            Preview component to show in this dialog.
     * @param isSolved
     *            If game was won or lost.
     * @param settings
     *            Settings object for color options.
     */
    public GameOverUI(final NonogramProvider pattern,
            final BoardPreview boardPreview, final boolean isSolved,
            final Settings settings) {

        super();

        this.pattern = pattern;
        this.boardPreview = boardPreview;
        this.isSolved = isSolved;
        this.settings = settings;

        initialize();

        nonogramNameLabel.setText(pattern.getName());
    }

    /**
     * Initializes dialog at the end of the game depending on whether the game
     * was won or lost.
     */
    private void initialize() {

        // TODO use proper layout manager and pack to decide about window size!
        final int width = 300;
        final int height = 300;
        if (isSolved) {
            setSize(width, height);
        } else {
            setSize(width, height / 2);
        }

        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(Messages.getString("GameOverUI.Title"));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setUndecorated(true);

        setContentPane(getJContentPane());
    }

    /**
     * Initializes the content pane for this dialog depending on whether game
     * was won or lost.
     * 
     * @return Content pane with all elements.
     */
    private JPanel getJContentPane() {
        
        setFont(FontFactory.createTextFont());

        if (jContentPane == null) {

            // gap sizes for layout
            final int horizontalGap = 100;
            final int verticalGap = 20;

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

            messageLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

            nonogramNameLabel = new JLabel();
            nonogramNameLabel.setText(pattern.getName());
            nonogramNameLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
            nonogramNameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            nonogramNameLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
            nonogramNameLabel.setVerticalAlignment(SwingConstants.CENTER);
            nonogramNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            jContentPane = new JPanel();

            FlowLayout layout = new FlowLayout();
            layout.setHgap(horizontalGap);
            layout.setVgap(verticalGap);
            jContentPane.setLayout(layout);
            jContentPane.add(messageLabel);

            if (isSolved) {

                jContentPane.add(nonogramNameLabel);
                jContentPane.add(boardPreview);
            }
            jContentPane.add(getJButton());

            jContentPane.setBackground(settings.getColorModel().getTopColor());
            jContentPane.setForeground(settings.getColorModel()
                    .getBottomColor());
            jContentPane.setBorder(BorderFactory.createEtchedBorder());
        }
        return jContentPane;
    }

    /**
     * Initializes the close button for this dialog.
     * 
     * @return Close button for this dialog.
     */
    private JButton getJButton() {

        if (closeButton == null) {

            closeButton = new JButton();
            closeButton.setText(Messages.getString("GameOverUI.CloseButton"));
            closeButton.grabFocus();
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

}
